package com.sing4u.kr.songrequest.infra;

import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RequestPeriodRepositoryImpl implements RequestPeriodRepository {

    private final JpaRequestPeriodRepository jpa;

    public RequestPeriodRepositoryImpl(JpaRequestPeriodRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public RequestPeriod save(RequestPeriod period) {
        return jpa.save(period);
    }

    @Override
    public Optional<RequestPeriod> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<RequestPeriod> findByArtistId(UUID artistId) {
        return jpa.findByArtistId(artistId);
    }

    interface JpaRequestPeriodRepository extends JpaRepository<RequestPeriod, UUID> {
        List<RequestPeriod> findByArtistId(UUID artistId);
    }
}