// 📁 application/query/GetSongRequestRankingQuery.java
package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.songrequest.application.dto.SongRankResponse;
import com.sing4u.kr.songrequest.domain.SongRequest;
import com.sing4u.kr.songrequest.domain.SongRequestRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GetSongRequestRankingQuery {

    private final SongRequestRepository repository;

    public GetSongRequestRankingQuery(SongRequestRepository repository) {
        this.repository = repository;
    }

    public List<SongRankResponse> execute(UUID requestPeriodId) {
        List<SongRequest> requests = repository.findByRequestPeriodId(requestPeriodId);

        Map<String, Long> grouped = requests.stream()
                .collect(Collectors.groupingBy(SongRequest::titleOfSong, Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(e -> new SongRankResponse(e.getKey(), e.getValue()))
                .toList();
    }
}