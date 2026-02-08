package com.sing4u.kr.songRequest.service;


import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songRequest.dto.response.*;
import com.sing4u.kr.songRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.songRequest.enums.SortType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongRequestService {

    private final SongRequestRepository songRequestRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final MusicPlatformFactory musicPlatformFactory;

    private record SongInfo(String title, String artistName, String platformTrackId, String platformName, String albumImageUrl){}

    private final UserService userService;

    private SongInfo determineSongInfo(SongRequestCreateDto createDto) {
        String title = createDto.getSongTitle();
        String artistName = createDto.getArtistName();
        String resolvedPlatformTrackId = createDto.getPlatformTrackId(); // 사용자가 제공한 트랙 ID
        String resolvedPlatformName = createDto.getMusicPlatformName(); // 사용자가 제공한 플랫폼 이름
        String albumImageUrl = null;

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
                        albumImageUrl = trackDetails.getAlbumImageUrl();

                        log.info("'{}' 플랫폼 정보로 곡 정보를 설정했습니다: '{}' - '{}' (ID: {}), image: {}",
                                resolvedPlatformName, title, artistName, resolvedPlatformTrackId, albumImageUrl);
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
        return new SongInfo(title, artistName, resolvedPlatformTrackId, resolvedPlatformName, albumImageUrl);
    }


    public SongRequestResponseDto createSongRequest(SongRequestCreateDto createDto) {
        // 1. Tags 리스트를 쉼표로 구분된 문자열로 안전하게 변환합니다. (null 처리 포함)
        String tagString = Optional.ofNullable(createDto.getTags())
                .filter(tags -> !tags.isEmpty())
                .map(tags -> String.join(", ", tags))
                .orElse("N/A"); // 태그 리스트가 null이거나 비어있으면 "N/A"로 대체

        // 2. URL이 null일 경우 "N/A"로 대체합니다.
        String urlString = Optional.ofNullable(createDto.getUrl())
                .orElse("N/A");

        log.info("곡 요청 처리 시작 - 입력된 곡 정보: '{}' - '{}', 플랫폼: {}, ID: {}, 태그: {}, URL: {}",
                createDto.getSongTitle(),
                createDto.getArtistName(),
                createDto.getMusicPlatformName(),
                createDto.getPlatformTrackId(),
                tagString, // 변환된 태그 문자열 사용
                urlString  // null 처리된 URL 문자열 사용
        );

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

        if (createDto.getTags() != null && createDto.getTags().size() > 10) {
            throw new ApiException(ExceptionCode.BAD_REQUEST, "태그는 최대 10개까지만 등록 가능합니다.");
        }

        // DB 작업: 엔티티 생성 및 저장
        SongRequest songRequest = SongRequest.builder()
                .session(session)
                .fanEmail(createDto.getEmail())
                .songTitle(songInfo.title())
                .songArtistName(songInfo.artistName())
                .musicPlatformName(songInfo.platformName())
                .platformTrackId(songInfo.platformTrackId())
                .tags(createDto.getTags())
                .url(createDto.getUrl())
                .albumImageUrl(songInfo.albumImageUrl())
                .likeCount(0)
                .build();

        SongRequest savedSongRequest = songRequestRepository.save(songRequest);
        log.info("신청곡 추천 등록 완료: ID {}, 제목: {}", savedSongRequest.getId(), savedSongRequest.getSongTitle());

        return new SongRequestResponseDto(savedSongRequest.getId(), "신청곡 추천 등록 완료");
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

        // 현재 오픈된 세션 (없을 수도 있음)
        Optional<Session> openSession = sessions.stream()
                .filter(s -> s.getStatus() == SessionStatus.OPEN)
                .max(Comparator.comparing(Session::getStartedAt));

        // DTO 변환
        List<SessionSongsDto> dtos = sessions.stream()
                .map(session -> {
                // step 1 곡제목 :: 아티스트 이름으로 그룹핑
                    Map<String, List<SongRequest>> grouped = session.getSongRequests().stream()
                            .collect(Collectors.groupingBy(
                                    req -> req.getSongTitle() + "::" + req.getSongArtistName()
                            ));

                    // step 2 그룹별로 생성
                    List<SongDetailDto> songDetails = grouped.entrySet().stream()
                            .map(entry -> {
                                List<SongRequest> requests = entry.getValue();
                                SongRequest latest = requests.get(requests.size() - 1);

                                return SongDetailDto.from(latest);
                            })
                            .sorted(Comparator.comparing(SongDetailDto::getRequestedAt))
                            .collect(Collectors.toList());

                    return SessionSongsDto.from(session, songDetails);
                })
                .filter(dto -> {
                    // 오픈된 세션은 무조건 포함
                    if (openSession.isPresent() && dto.getSessionId().equals(openSession.get().getId())) {
                        return true;
                    }
                    // 나머지는 곡이 있어야만 포함
                    return dto.getSongs() != null && !dto.getSongs().isEmpty();
                })
                .collect(Collectors.toList());

        // 정렬: 현재 오픈된 세션을 list[0]으로 보장
        if (openSession.isPresent()) {
            dtos.sort((a, b) -> {
                if (a.getSessionId().equals(openSession.get().getId())) return -1;
                if (b.getSessionId().equals(openSession.get().getId())) return 1;
                return b.getStartedAt().compareTo(a.getStartedAt()); // 최신순 정렬
            });
        } else {
            dtos.sort(Comparator.comparing(SessionSongsDto::getStartedAt).reversed());
        }

        return dtos;
    }

    @Transactional(readOnly = true)
    public ArtistSongRequestsResponse getArtistSongRequests(
            Long artistId,
            Long sessionId,
            String keyword,
            SortType sort,
            int page,
            int pageSize
    ) {
        // 1) 아티스트의 모든 세션 + 신청곡 로딩
        List<Session> sessions = sessionRepository.findAllWithSongRequestsByArtist(artistId);

        // 2) SongRequest 평탄화
        Stream<SongRequest> stream = sessions.stream()
                .flatMap(session -> session.getSongRequests().stream());

        // 불러준 곡 제외
        stream = stream.filter(req -> !"Y".equals(req.getCalledYn()));

        // 3) sessionId 필터링
        if (sessionId != null) {
            stream = stream.filter(req -> req.getSession().getId().equals(sessionId));
        }

        // 4) keyword 필터링 (제목 / 가수명 / 태그 검색)
        if (keyword != null && !keyword.isBlank()) {
            String lower = keyword.toLowerCase(Locale.ROOT);

            stream = stream.filter(req -> {
                boolean titleMatch =
                        req.getSongTitle() != null &&
                                req.getSongTitle().toLowerCase(Locale.ROOT).contains(lower);

                boolean artistMatch =
                        req.getSongArtistName() != null &&
                                req.getSongArtistName().toLowerCase(Locale.ROOT).contains(lower);

                boolean tagMatch = false;
                if (req.getTags() != null) {
                    tagMatch = req.getTags().stream()
                            .filter(Objects::nonNull)
                            .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(lower));
                }

                return titleMatch || artistMatch || tagMatch;
            });
        }

        // 5) 정렬
        Comparator<SongRequest> comparator;

        if (sort == SortType.POPULAR) {
            // 인기순: 좋아요 많은 순
            comparator = Comparator.comparing(SongRequest::getLikeCount).reversed();
        } else {
            // 기본 = 최신순
            comparator = Comparator.comparing(SongRequest::getRequestedAt).reversed();
        }

        List<SongDetailDto> all = stream
                .sorted(comparator)
                .map(SongDetailDto::from)
                .toList();

        // 6) 페이징
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(pageSize, 1);

        int from = safePage * safeSize;
        if (from >= all.size()) {
            return ArtistSongRequestsResponse.builder()
                    .songs(Collections.emptyList())
                    .hasNext(false)
                    .page(safePage)
                    .pageSize(safeSize)
                    .build();
        }

        int to = Math.min(from + safeSize, all.size());
        boolean hasNext = to < all.size();

        List<SongDetailDto> resultPage = all.subList(from, to);

        return ArtistSongRequestsResponse.builder()
                .songs(resultPage)
                .hasNext(hasNext)
                .page(safePage)
                .pageSize(safeSize)
                .build();
    }

    @Transactional
    public void changeSaveStatus(Long songRequestId, boolean saved) {
        SongRequest songRequest = songRequestRepository.findById(songRequestId)
                .orElseThrow(() ->
                        new ApiException(ExceptionCode.NOT_FOUND, "신청곡을 찾을 수 없습니다. ID: " + songRequestId)
                );

        if (saved) {
            songRequest.markSaved();     // savedYn = Y
        } else {
            songRequest.unmarkSaved();   // savedYn = N
        }
        // updatedAt은 Auditing으로 *저장 순 조회가 가능하도록 자동 갱신

    }

    @Transactional(readOnly = true)
    public SongRequestManageResponseDto getArtistSongRequestsForManage(
            Long artistId,
            int page,
            int pageSize,
            String keyword
    ) {
        // 1) 아티스트 존재 확인
        userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        // 2) 페이징
        var pageable = PageRequest.of(page, pageSize);

        // 3) 집계 조회
        Page<SongRequestManageItemDto> pageResult =
                songRequestRepository.findArtistSongRequestsForManage(artistId, keyword, pageable);

        // 4) 명세 형태로 응답 래핑
        return SongRequestManageResponseDto.of(pageResult);
    }

    @Transactional
    public SongRequestCalledResponse markSongRequestCalled(Long artistId, Long songRequestId) {
        SongRequest songRequest = songRequestRepository
                .findByIdAndSession_Artist_Id(songRequestId, artistId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "신청곡을 찾을 수 없습니다. ID: " + songRequestId));
        // or 권한 예외로 분리해도 됨

        if (!"Y".equals(songRequest.getCalledYn())) {
            songRequest.markCalled();
        }

        return new SongRequestCalledResponse(songRequest.getId(), songRequest.getCalledYn());
    }
}