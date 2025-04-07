package com.sing4u.kr.songrequest.infra;

import com.sing4u.kr.songrequest.domain.SongRequest;
import com.sing4u.kr.songrequest.domain.SongRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SongRequestRepositoryImpl implements SongRequestRepository {

    private final SongRequestJpaRepository jpa;

    @Override
    public SongRequest save(SongRequest request) {
        return jpa.save(request);
    }

    @Override
    public Optional<SongRequest> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<SongRequest> findByRequestPeriodId(UUID requestPeriodId) {
        return jpa.findByRequestPeriodId(requestPeriodId);
    }

    @Override
    public List<SongRequest> findByRequesterId(UUID requesterId) {
        return jpa.findByRequesterId(requesterId);
    }

    interface JpaSongRequestRepository extends JpaRepository<SongRequest, UUID> {
        List<SongRequest> findByRequestPeriodId(UUID requestPeriodId);
        List<SongRequest> findByRequesterId(UUID requesterId);
    }
}