package com.sing4u.kr.music.spotify; // 사용자님의 패키지 경로

import com.neovisionaries.i18n.CountryCode;
import com.sing4u.kr.music.MusicInterface; // 사용자님의 인터페이스 경로
import com.sing4u.kr.music.dto.TrackDto;    // 공통 DTO 경로
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service("spotifyMusicService")
@RequiredArgsConstructor
public class SpotifyMusicService implements MusicInterface {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyMusicService.class);
    private final SpotifyApi spotifyApi; // SpotifyConfig에서 생성된 빈 주입

    public static final String PLATFORM_IDENTIFIER = "SPOTIFY"; // 플랫폼 식별자 상수

    private String accessToken;
    private Instant tokenExpiry;

    private boolean isTokenValid() {
        return accessToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60));
    }

    @PostConstruct
    public void accessToken() {
        try {
            ClientCredentials credentials = spotifyApi.clientCredentials().build().execute();
            accessToken = credentials.getAccessToken();
            tokenExpiry = Instant.now().plusSeconds(credentials.getExpiresIn());

            spotifyApi.setAccessToken(accessToken);
            logger.info("Spotify 액세스 토큰 발급 완료. {}초 후 만료 예정", credentials.getExpiresIn());
        } catch (Exception e) {
            logger.error("Spotify 토큰 발급 실패: {}", e.getMessage(), e);
        }
    }

    private void tokenRefreshed() {
        if (!isTokenValid()) {
            logger.info("Spotify 토큰이 유효하지 않음. 갱신 시도.");
            accessToken();
        }
    }

    @Override
    public String getPlatformIdentifier() {
        return PLATFORM_IDENTIFIER;
    }

    @Override
    public TrackDto getTrackDetails(String platformTrackId) {
        if (platformTrackId == null || platformTrackId.isBlank()) {
            logger.warn("트랙 ID가 비어있어 Spotify API 호출을 생략합니다.");
            return null;
        }

        tokenRefreshed();
        if (spotifyApi.getAccessToken() == null) {
            return null;
        }

        try {
            Track track = spotifyApi.getTrack(platformTrackId).build().execute();
            String artistNames = Arrays.stream(track.getArtists())
                    .map(ArtistSimplified::getName)
                    .collect(Collectors.joining(", "));

            return TrackDto.builder()
                    .platformTrackId(track.getId())
                    .title(track.getName())
                    .artistName(artistNames)
                    .platformName(PLATFORM_IDENTIFIER)
                    .build();
        } catch (SpotifyWebApiException e) {
            logger.error("Spotify API 오류 (트랙 ID: {}): {}", platformTrackId, e.getMessage());
            if (e.getMessage().toLowerCase().contains("token") || e.getClass().getSimpleName().contains("Unauthorized")) {
                logger.info("토큰 오류로 판단되어 재발급 시도");
                accessToken();
            }
        } catch (IOException e) {
            logger.error("I/O 오류 발생: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<TrackDto> searchTracks(String query, int limit, int offset, String market) {
        try {
            SearchTracksRequest.Builder builder = spotifyApi.searchTracks(query).limit(limit).offset(offset);

            CountryCode code = CountryCode.KR; // 기본값 KR
            if (market != null && !market.isBlank()) {
                try {
                    code = CountryCode.valueOf(market.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    logger.warn("Unknown market code '{}', falling back to KR.", market);
                }
            }
            builder.market(code);

            Paging<Track> paging = builder.build().execute();

            return Arrays.stream(paging.getItems())
                    .map(t -> {
                        String artistNames = Arrays.stream(t.getArtists())
                                .map(ArtistSimplified::getName)
                                .collect(Collectors.joining(", "));

                        String coverUrl = getCoverUrl(t);

                        return TrackDto.builder()
                                .platformTrackId(t.getId())
                                .title(t.getName())
                                .artistName(artistNames)
                                .platformName(getPlatformIdentifier())
                                .albumImageUrl(coverUrl)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (SpotifyWebApiException e) {
            logger.error("Spotify API error on searchTracks('{}'): {}", query, e.getMessage());
            accessToken();
        } catch (IOException e) {
            logger.error("I/O error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
        return List.of();
    }

    private static String getCoverUrl(Track t) {
        String coverUrl = null;
        AlbumSimplified album = t.getAlbum();
        if (album != null && album.getImages() != null && album.getImages().length > 0) {
            Image[] images = album.getImages(); // 보통 [640, 300, 64] 순서
            if (images.length >= 2) {
                coverUrl = images[1].getUrl();   // 중간(대부분 300px)
            } else {
                coverUrl = images[0].getUrl();   // 1장만 있으면 그거 사용
            }
        }
        return coverUrl;
    }
}