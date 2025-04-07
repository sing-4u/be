package com.sing4u.kr.songrequest.infra;

import com.sing4u.kr.songrequest.domain.RequestPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestPeriodJpaRepository  extends JpaRepository<RequestPeriod, UUID> {
    List<RequestPeriod> findByArtistId(UUID artistId);
}
