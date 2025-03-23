package com.sing4u.kr.songrequest.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestPeriodRepository {
    RequestPeriod save(RequestPeriod period);
    Optional<RequestPeriod> findById(UUID id);
    List<RequestPeriod> findByArtistId(UUID artistId);
}
