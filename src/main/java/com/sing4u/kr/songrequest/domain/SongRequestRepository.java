package com.sing4u.kr.songrequest.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRequestRepository {
    SongRequest save(SongRequest request);
    Optional<SongRequest> findById(UUID id);
    List<SongRequest> findByRequestPeriodId(UUID requestPeriodId);
    List<SongRequest> findByRequesterId(UUID requesterId);
}