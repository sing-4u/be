package com.sing4u.kr.songrequest.ui;

import com.sing4u.kr.songrequest.application.command.CreateRequestPeriodUseCase;
import com.sing4u.kr.songrequest.application.dto.CreateRequestPeriodCommand;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.application.dto.SongRankResponse;
import com.sing4u.kr.songrequest.application.query.GetSongRequestRankingQuery;
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
    private final GetSongRequestRankingQuery getSongRequestRankingQuery;


    @PostMapping
    public ResponseEntity<RequestPeriodResponse> create(@RequestBody CreateRequestPeriodCommand command) {
        return ResponseEntity.ok(createRequestPeriodUseCase.execute(command));
    }

    @GetMapping("/{periodId}/requests")
    public ResponseEntity<List<SongRankResponse>> getRequests(@PathVariable UUID periodId) {
        return ResponseEntity.ok(getSongRequestRankingQuery.execute(periodId));
    }
}