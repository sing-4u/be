package com.sing4u.kr.session.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.session.dto.response.ArtistSessionResponseDto;
import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.service.SessionService;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.Paging;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "세션 시작", description = "아티스트가 신청곡 받기를 시작합니다.")
    @PostMapping("/{artistPublicId}/sessions")
    public ResponseResult<SessionResponseDto> startSession(
            @Parameter(description = "세션을 시작할 아티스트의 공개 ID", example = "user_public_id_3")
            @PathVariable String artistPublicId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto sessionResponse = sessionService.createSession(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @Operation(summary = "세션 종료", description = "신청곡 받기를 종료 처리합니다.")
    @PatchMapping("/{artistPublicId}/sessions/{sessionId}/close")
    public ResponseResult<SessionResponseDto> closeSession(
            @Parameter(description = "세션을 종료할 아티스트의 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId,
            @Parameter(description = "종료할 세션의 ID", example = "1")
            @PathVariable Long sessionId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto sessionResponse = sessionService.closeSession(artistId, sessionId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @Operation(summary = "현재 오픈된 세션 조회", description = "아티스트의 현재 진행 중인 세션 정보를 조회합니다.")
    @GetMapping("/{artistPublicId}/sessions/open")
    public ResponseResult<CurrentSessionResponseDto> getArtistCurrentOpenSession(
            @Parameter(description = "세션 정보를 조회할 아티스트의 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);

        User user = userRepository.findByUserPublicIdAndDeletedAtIsNull(artistPublicId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다."));
//        Boolean sessionOpenClose =  user.isOpen();

//        CurrentSessionResponseDto currentSession = sessionService.getArtistOpenSession(artistId, sessionOpenClose);
        CurrentSessionResponseDto currentSession = sessionService.getArtistSessionStatus(artistId);

        if (currentSession == null) {
            return new ResponseResult<>(ResponseCode.SUCCESS, "오픈된 세션이 없습니다.", null);
        }

        return new ResponseResult<>(ResponseCode.SUCCESS, currentSession);
    }

    @GetMapping("/{artistPublicId}/sessions")
    @Operation(summary = "아티스트의 세션 목록 조회",
            description = "page/pageSize로 페이지네이션, 최신 순으로 정렬")
    public ResponseResult<PagingResponse<SessionResponseDto>> getSessions(
            @Parameter(description = "세션 목록을 조회할 아티스트의 공개 ID", example = "user_public_id_1") @PathVariable String artistPublicId,
            @Parameter(description = "현재 페이지(0-base)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int pageSize
    ) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);

        // 정렬: 최신 시작 시각
        var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "startedAt"));

        Page<SessionResponseDto> dtoPage = sessionService
                .getSessionsByArtist(artistId, pageable)
                .map(SessionResponseDto::from);

        return new ResponseResult<>(ResponseCode.SUCCESS, PagingResponse.of(dtoPage));
    }

    @GetMapping("/{artistPublicId}/sessions/manage")
    @Operation(
            summary = "세션 목록 조회(아티스트용)",
            description = "세션별 신청곡 수를 포함한 아티스트 관리용 세션 목록 조회"
    )
    public ResponseResult<PagingResponse<ArtistSessionResponseDto>> getArtistSessionsForManage(
            @Parameter(description = "아티스트 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId,
            @Parameter(description = "현재 페이지(0-base)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);

        var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "startedAt"));

        Page<ArtistSessionResponseDto> dtoPage =
                sessionService.getArtistSessionsForManage(artistId, pageable);

        return new ResponseResult<>(ResponseCode.SUCCESS, PagingResponse.of(dtoPage));
    }

}
