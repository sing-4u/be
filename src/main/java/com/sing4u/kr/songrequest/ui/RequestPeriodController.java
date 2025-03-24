package com.sing4u.kr.songrequest.ui;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.songrequest.application.command.CloseRequestPeriodUseCase;
import com.sing4u.kr.songrequest.application.command.CreateRequestPeriodUseCase;
import com.sing4u.kr.songrequest.application.dto.CreateRequestPeriodCommand;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.application.dto.SongRankResponse;
import com.sing4u.kr.songrequest.application.query.GetPagedSongRequestRankingQuery;
import com.sing4u.kr.songrequest.application.query.GetRequestPeriodsByArtistQuery;
import com.sing4u.kr.songrequest.application.query.dto.RequestPeriodView;
import com.sing4u.kr.songrequest.application.query.dto.SongRankingView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/request-periods")
@RequiredArgsConstructor
public class RequestPeriodController {

    private final CreateRequestPeriodUseCase createRequestPeriodUseCase;
    private final CloseRequestPeriodUseCase closeRequestPeriodUseCase;
    private final GetPagedSongRequestRankingQuery getPagedSongRequestRankingQuery;
    private final GetRequestPeriodsByArtistQuery query;


    @PostMapping
    public ResponseEntity<RequestPeriodResponse> create(@RequestBody CreateRequestPeriodCommand command) {
        return ResponseEntity.ok(createRequestPeriodUseCase.execute(command));
    }

    @GetMapping
    public ResponseEntity<List<RequestPeriodView>> getByArtist(@RequestParam UUID artistId) {
        return ResponseEntity.ok(query.execute(artistId));
    }
    @GetMapping("/{periodId}/requests")
    public ResponseEntity<PagingResponse<SongRankingView>> getPagedSongRequests(
            @PathVariable UUID periodId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(getPagedSongRequestRankingQuery.execute(periodId, page-1, size));
    }


    @PatchMapping("/{periodId}/close")
    public ResponseEntity<RequestPeriodResponse> close(@PathVariable UUID periodId) {
        return ResponseEntity.ok(closeRequestPeriodUseCase.execute(periodId));
    }
}