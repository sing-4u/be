package com.sing4u.kr.spotify.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    private final SpotifyApi spotifyApi;

    @PostConstruct
    public void accessToken() {
        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();
            ClientCredentials credentials = clientCredentialsFuture.join();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            logger.info("Spotify 액세스 토큰을 발급받았습니다. 만료 시간: {}초", credentials.getExpiresIn());
        } catch (Exception e) {
            logger.error("Spotify 액세스 토큰 발급 중 오류 발생: {}. Spotify 기능 사용이 불가능할 수 있습니다.", e.getMessage(), e);
        }
    }

    public Track getTrackInfo(String trackId) {
        if (trackId == null || trackId.isBlank()) {
            logger.warn("트랙 ID가 null이거나 비어있습니다. Spotify API 호출을 건너뜁니다.");
            return null;
        }
        if (spotifyApi.getAccessToken() == null) {
            logger.warn("Spotify 액세스 토큰을 사용할 수 없습니다. 토큰 발급을 시도합니다.");
            accessToken();
            if (spotifyApi.getAccessToken() == null) {
                logger.error("Spotify 액세스 토큰 발급/갱신에 실패했습니다. 트랙 ID {}에 대한 정보를 가져올 수 없습니다.", trackId);
                return null;
            }
        }

        try {
            GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
            final CompletableFuture<Track> trackFuture = getTrackRequest.executeAsync();
            Track track = trackFuture.join();
            if (track != null) {
                logger.info("Spotify에서 트랙 정보를 성공적으로 가져왔습니다: {} - {}", track.getName(), track.getId());
            }
            return track;
        }
        catch (CompletionException e) { // CompletableFuture.join() 에서 발생할 수 있는 예외
            Throwable cause = e.getCause(); // 실제 원인 예외를 가져옴
            logger.error("트랙 ID {}에 대한 비동기 Spotify API 호출 중 오류 발생 (CompletionException): {}",
                    trackId, (cause != null ? cause.getMessage() : e.getMessage()), (cause != null ? cause : e));

            if (cause instanceof SpotifyWebApiException) {
                SpotifyWebApiException swe = (SpotifyWebApiException) cause;
                logger.error("트랙 ID {}의 CompletionException에 포함된 Spotify Web API 오류: {} (오류 유형: {})",
                        trackId, swe.getMessage(), swe.getClass().getSimpleName());

                String errorMessage = swe.getMessage() != null ? swe.getMessage().toLowerCase() : "";
                if (errorMessage.contains("token expired") || errorMessage.contains("unauthorized") || swe.getClass().getSimpleName().contains("UnauthorizedException")) {
                    logger.info("트랙 ID {}에 대한 Spotify 토큰이 만료되었거나 유효하지 않을 수 있습니다 (CompletionException). 토큰 갱신을 시도합니다.", trackId);
                    accessToken();
                } else if (errorMessage.contains("rate limit")) {
                    logger.warn("트랙 ID {}에 대한 Spotify API 호출 제한을 초과한 것 같습니다 (CompletionException).", trackId);
                }
            } else if (cause instanceof IOException) {
                logger.error("트랙 ID {}의 CompletionException에 포함된 I/O 오류: {}", trackId, cause.getMessage());
            }
        } catch (CancellationException e) {
            logger.warn("트랙 ID {}에 대한 Spotify API 호출이 취소되었습니다: {}", trackId, e.getMessage(), e);
        }
        return null; // 예외 발생 시 null 반환
    }

    public String getArtistNamesFromTrack(Track track) {
        if (track == null || track.getArtists() == null || track.getArtists().length == 0) {
            return "";
        }
        return Arrays.stream(track.getArtists())
                .map(artistSimplified -> artistSimplified.getName())
                .collect(Collectors.joining(", "));
    }
}