package com.sing4u.kr.songrequest.domain;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
@Table(name = "request_periods")
public class RequestPeriod {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID artistId;


    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    protected RequestPeriod() {
    }

    public static RequestPeriod create(UUID artistId, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ApiException(ExceptionCode.BAD_REQUEST, "시작일은 종료일보다 앞서야 합니다.");
        }
        RequestPeriod period = new RequestPeriod();
        period.artistId = artistId;
        period.startAt = start;
        period.endAt = end;
        period.createdAt = LocalDateTime.now();
        return period;
    }

    public boolean isOpen(LocalDateTime now) {
        return now.isAfter(startAt) && now.isBefore(endAt);
    }

    public boolean isOpenAt(LocalDateTime time) {
        return time.isAfter(startAt) && time.isBefore(endAt);
    }

    public boolean isExpiredAt(LocalDateTime time) {
        return time.isAfter(endAt);
    }

    public boolean hasStartedAt(LocalDateTime time) {
        return time.isAfter(startAt);
    }

    public boolean isCurrent(LocalDateTime time) {
        return isOpenAt(time);
    }

    public UUID getId() {
        return this.id;
    }

    public UUID artistIdentifier() {
        return this.artistId;
    }

    public LocalDateTime openedAt() {
        return this.startAt;
    }

    public LocalDateTime closedAt() {
        return this.endAt;
    }

    public LocalDateTime createdOn() {
        return this.createdAt;
    }
}
