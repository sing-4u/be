package com.sing4u.kr.songRequest.like.controller;

import com.sing4u.kr.common.auth.LoginUserId;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.songRequest.like.dto.response.SongRequestLikeResponse;
import com.sing4u.kr.songRequest.like.service.SongRequestLikeService;
import com.sing4u.kr.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/song-requests/{songRequestId}/likes")
@RequiredArgsConstructor
public class SongRequestLikeController {
    private final SongRequestLikeService service;

    @Operation(
            summary = "좋아요 추가",
            description = """
    해당 신청곡에 좋아요를 누릅니다(멱등).
    이미 눌린 상태여도 에러 없이 성공으로 처리하며,
    최신 상태(liked=true, likeCount)를 반환합니다.
    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            {
              "code": "0001",
              "message": "성공",
              "data": { "liked": true, "likeCount": 53 }
            }
            """))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "UNAUTHORIZED", "message": "로그인이 필요합니다." }
            """))),
            @ApiResponse(responseCode = "404", description = "신청곡 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "NOT_FOUND", "message": "신청곡을 찾을 수 없습니다." }
            """)))
    })
    @PostMapping
    public ResponseResult<SongRequestLikeResponse> like(
            @PathVariable Long requestId,
            @LoginUserId Long userId) {
        return new ResponseResult<>(service.like(requestId, userId));
    }

    @Operation(
            summary = "좋아요 취소",
            description = """
    해당 신청곡의 좋아요를 취소합니다(멱등).
    이미 좋아요가 없는 상태여도 에러 없이 성공으로 처리하며,
    최신 상태(liked=false, likeCount)를 반환합니다.
    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            {
              "code": "0001",
              "message": "성공",
              "data": { "liked": false, "likeCount": 52 }
            }
            """))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "UNAUTHORIZED", "message": "로그인이 필요합니다." }
            """))),
            @ApiResponse(responseCode = "404", description = "신청곡 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "NOT_FOUND", "message": "신청곡을 찾을 수 없습니다." }
            """)))
    })
    @DeleteMapping
    public ResponseResult<SongRequestLikeResponse> unlike(
            @PathVariable Long requestId,
            @LoginUserId Long userId) {
        return new ResponseResult<>(service.unlike(requestId, userId));
    }

    @Operation(
            summary = "좋아요 상태 조회",
            description = """
    현재 로그인한 사용자가 해당 신청곡에 좋아요를 눌렀는지 여부와
    현재 좋아요 수를 조회합니다.
    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            {
              "code": "0001",
              "message": "성공",
              "data": { "liked": true, "likeCount": 52 }
            }
            """))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "UNAUTHORIZED", "message": "로그인이 필요합니다." }
            """))),
            @ApiResponse(responseCode = "404", description = "신청곡 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
            { "code": "NOT_FOUND", "message": "신청곡을 찾을 수 없습니다." }
            """)))
    })
    @GetMapping
    public ResponseResult<SongRequestLikeResponse> status(
            @PathVariable Long requestId,
            @LoginUserId Long userId) {
        return new ResponseResult<>(service.getStatus(requestId, userId));
    }
}
