package com.sing4u.kr.songrequest.infra;

import com.sing4u.kr.songrequest.domain.SongRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SongRequestJpaRepository extends JpaRepository<SongRequest, UUID> {
    List<SongRequest> findByRequestPeriodId(UUID requestPeriodId);
    List<SongRequest> findByRequesterId(UUID requesterId);
}
