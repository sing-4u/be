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

import com.sing4u.kr.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    private final UserService userService;

    private SongInfo determineSongInfo(SongRequestCreateDto createDto) {
        String title = createDto.getSongTitle();
        String artistName = createDto.getArtistName();
        String resolvedPlatformTrackId = createDto.getPlatformTrackId(); // 사용자가 제공한 트랙 ID
        String resolvedPlatformName = createDto.getMusicPlatformName(); // 사용자가 제공한 플랫폼 이름

        // 플랫폼 트랙 ID가 존재할 경우에만 외부 API 호출
        if (resolvedPlatformTrackId != null && !resolvedPlatformTrackId.isBlank()) {
            Optional<MusicInterface> optionalService = musicPlatformFactory.getService(resolvedPlatformName);
            if (optionalService.isPresent()) {
                MusicInterface platformService = optionalService.get();
                log.info("'{}' 플랫폼의 트랙 ID '{}'로 정보 조회를 시도합니다.",
                        resolvedPlatformName, resolvedPlatformTrackId);

                try {
                    TrackDto trackDetails = platformService.getTrackDetails(resolvedPlatformTrackId);
                    if (trackDetails != null) {
                        if (trackDetails.getTitle() != null && !trackDetails.getTitle().isBlank()) {
                            title = trackDetails.getTitle();
                        }
                        if (trackDetails.getArtistName() != null && !trackDetails.getArtistName().isBlank()) {
                            artistName = trackDetails.getArtistName();
                        }
                        resolvedPlatformTrackId = trackDetails.getPlatformTrackId();
                        resolvedPlatformName = trackDetails.getPlatformName();

                        log.info("'{}' 플랫폼 정보로 곡 정보를 설정했습니다: '{}' - '{}' (ID: {})",
                                resolvedPlatformName, title, artistName, resolvedPlatformTrackId);
                    } else {
                        log.warn("'{}' 플랫폼에서 트랙 ID '{}'에 대한 정보를 가져오지 못했습니다. DTO에 입력된 곡 정보를 우선 사용합니다.",
                                resolvedPlatformName, resolvedPlatformTrackId);
                    }
                } catch (Exception e) {
                    log.error("플랫폼 '{}'에서 트랙 ID '{}' 조회 중 예외 발생. 입력 정보로 계속 진행합니다.",
                            resolvedPlatformName, resolvedPlatformTrackId, e);
                }
            }
        }

        // 플랫폼 정보가 없거나 트랙 ID가 없는 경우 DTO 정보 그대로 사용
        return new SongInfo(title, artistName, resolvedPlatformTrackId, resolvedPlatformName);
    }


    public SongRequestResponseDto createSongRequest(SongRequestCreateDto createDto) {
        log.info("곡 요청 처리 시작 - 입력된 곡 정보: '{}' - '{}', 플랫폼: {}, ID: {}",
                createDto.getSongTitle(), createDto.getArtistName(),
                createDto.getMusicPlatformName(), createDto.getPlatformTrackId());

        // STEP 1: 외부 API 호출 등 트랜잭션이 불필요한 작업을 먼저 수행.
        SongInfo determinedSongInfo = determineSongInfo(createDto);

        log.info("최종 결정된 곡 정보: '{}' - '{}', 플랫폼: {}, ID: {}",
                determinedSongInfo.title(), determinedSongInfo.artistName(),
                determinedSongInfo.platformName(), determinedSongInfo.platformTrackId());

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

        Long artistId = userService.getUserIdByPublicId(createDto.getArtistPublicId());
        validateSession(session, artistId);

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
                    // step 1 곡 제목 :: 아티스트이름 으로 그룹핑
                    Map<String, List<SongRequest>> grouped = session.getSongRequests().stream()
                            .collect(Collectors.groupingBy(
                                    req -> req.getSongTitle() + "::" + req.getSongArtistName()
                            ));

                    // step 2 그룹별로 생성
                    List<SongDetailDto> songDetails = grouped.entrySet().stream()
                            .map(entry -> {
                                List<SongRequest> requests = entry.getValue();
                                SongRequest latest = requests.get(requests.size() - 1);

                                return SongDetailDto.from(latest, (long) requests.size());
                            })
                            .sorted(Comparator.comparing(SongDetailDto::getRequestedAt))
                            .collect(Collectors.toList());
                    return SessionSongsDto.from(session, songDetails);
                })
                .collect(Collectors.toList());
    }
}