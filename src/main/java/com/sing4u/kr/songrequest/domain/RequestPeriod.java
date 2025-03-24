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

    @Enumerated(EnumType.STRING)
    private RequestPeriodStatus requestPeriodStatus;

    private LocalDateTime createdAt = LocalDateTime.now();


    protected RequestPeriod() {
    }

    public static RequestPeriod create(UUID artistId, LocalDateTime start) {
        if(start == null) throw new ApiException(ExceptionCode.BAD_REQUEST, "시작일은 필수 값입니다.");
        RequestPeriod period = new RequestPeriod();
        period.artistId = artistId;
        period.startAt = start;
        period.createdAt = LocalDateTime.now();
        period.requestPeriodStatus = RequestPeriodStatus.OPEN;
        return period;
    }

    public boolean isOpen() {
        return this.requestPeriodStatus == RequestPeriodStatus.OPEN;
    }

    public void close() {
        if(this.requestPeriodStatus != RequestPeriodStatus.OPEN) throw new ApiException(ExceptionCode.BAD_REQUEST, "이미 종료되었습니다.");
        this.requestPeriodStatus = RequestPeriodStatus.CLOSED;
        this.endAt = LocalDateTime.now();
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

    public RequestPeriodStatus status() {
        return this.requestPeriodStatus;
    }
}
