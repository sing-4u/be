package com.sing4u.kr.songRequest.service;


import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.dto.response.SongDetailDto;
import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.songRequest.repository.SongRequestRepository;
import com.sing4u.kr.music.MusicInterface;
import com.sing4u.kr.music.MusicPlatformFactory;
import com.sing4u.kr.music.dto.TrackDto;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.session.dto.SessionSongsDto;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.entity.enums.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongRequestService {

    private final SongRequestRepository songRequestRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final MusicPlatformFactory musicPlatformFactory;

    private record SongInfo(String title, String artistName, String platformTrackId, String platformName){}

    private SongInfo determineSongInfo(SongRequestCreateDto createDto) {
        String title = createDto.getSongTitle();
        String artistName = createDto.getArtistName();
        String resolvedPlatformTrackId = createDto.getPlatformTrackId(); // 사용자가 제공한 플랫폼 ID
        String resolvedPlatformName = createDto.getMusicPlatformName();

        if (resolvedPlatformName != null && !resolvedPlatformName.isBlank() &&
                resolvedPlatformTrackId != null && !resolvedPlatformTrackId.isBlank()) {

            Optional<MusicInterface> selectedServiceOpt = musicPlatformFactory.getService(resolvedPlatformName);

            if (selectedServiceOpt.isPresent()) {
                MusicInterface platformService = selectedServiceOpt.get();
                log.info("'{}' 플랫폼의 트랙 ID '{}'로 정보 조회를 시도합니다.", resolvedPlatformName, resolvedPlatformTrackId);

                TrackDto trackDetails = platformService.getTrackDetails(resolvedPlatformTrackId);

                if (trackDetails != null) {
                    title = trackDetails.getTitle();
                    artistName = trackDetails.getArtistName();
                    resolvedPlatformTrackId = trackDetails.getPlatformTrackId();
                    resolvedPlatformName = trackDetails.getPlatformName();
                    log.info("'{}' 플랫폼 정보로 곡 정보를 설정했습니다: '{}' - '{}' (ID: {})", resolvedPlatformName, title, artistName, resolvedPlatformTrackId);
                } else {
                    log.warn("'{}' 플랫폼에서 트랙 ID '{}'에 대한 정보를 가져오지 못했습니다. DTO에 입력된 곡 정보를 우선 사용합니다.", createDto.getMusicPlatformName(), createDto.getPlatformTrackId());
                }
            } else {
                log.warn("지원하지 않는 음악 플랫폼('{}')이거나, DTO에 잘못된 정보가 입력되었습니다. DTO의 곡 정보를 사용합니다.", resolvedPlatformName);
            }
        }
        return new SongInfo(title, artistName, resolvedPlatformTrackId, resolvedPlatformName);
    }

    public SongRequestResponseDto createSongRequest(SongRequestCreateDto createDto) {
        // STEP 1: 외부 API 호출 등 트랜잭션이 불필요한 작업을 먼저 수행.
        SongInfo determinedSongInfo = determineSongInfo(createDto);

        // STEP 2: 순수 DB 작업만 처리하는 트랜잭션 메서드를 호출.
        return createAndSaveSongRequestInTx(createDto, determinedSongInfo);
    }

    @Transactional
    public SongRequestResponseDto createAndSaveSongRequestInTx(SongRequestCreateDto createDto, SongInfo songInfo) {

        // 유효성 검사 1: 곡 정보 확인
        if (songInfo.title() == null || songInfo.title().isBlank() ||
                songInfo.artistName() == null || songInfo.artistName().isBlank()) {
            throw new IllegalArgumentException("곡 제목과 아티스트 이름은 필수입니다. 외부 플랫폼에서 정보를 가져오지 못했고, 직접 입력된 정보도 없습니다.");
        }

        // 유효성 검사 2: 세션 확인
        Session session = sessionRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "세션을 찾을 수 없습니다. ID: " + createDto.getSessionId()));

        validateSession(session, createDto.getArtistId());

        // DB 작업: 엔티티 생성 및 저장
        SongRequest songRequest = SongRequest.builder()
                .session(session)
                .fanEmail(createDto.getEmail())
                .songTitle(songInfo.title())
                .songArtistName(songInfo.artistName())
                .musicPlatformName(songInfo.platformName())
                .platformTrackId(songInfo.platformTrackId())
                .build();

        SongRequest savedSongRequest = songRequestRepository.save(songRequest);
        log.info("신청곡 등록 완료: ID {}, 제목: {}", savedSongRequest.getId(), savedSongRequest.getSongTitle());

        return new SongRequestResponseDto(savedSongRequest.getId(), "신청곡 등록 완료");
    }

    private void validateSession(Session session, Long requestArtistId) {
        if (!session.getArtist().getId().equals(requestArtistId)) {
            throw new ApiException(ExceptionCode.CONFLICT, "세션이 지정된 아티스트에게 속하지 않습니다.");
        }
        if (session.getStatus() != SessionStatus.OPEN) {
            throw new ApiException(ExceptionCode.CONFLICT, "이 세션은 현재 신청곡을 받고 있지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<SessionSongsDto> getSongRequestsByArtist(Long artistId) {
        userRepository.findByIdAndUserType(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND,"아티스트를 찾을 수 없습니다. ID: " + artistId));

        List<Session> sessions = sessionRepository.findAllWithSongRequestsByArtist(artistId);

        return sessions.stream()
                .map(session -> {
                    List<SongDetailDto> songDetails = session.getSongRequests().stream()
                            .sorted(Comparator.comparing(SongRequest::getRequestedAt))
                            .map(SongDetailDto::from)
                            .collect(Collectors.toList());

                    return SessionSongsDto.from(session, songDetails);
                })
                .collect(Collectors.toList());
    }
}