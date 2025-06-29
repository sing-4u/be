package com.sing4u.kr.songRequest.repository;

import com.sing4u.kr.songRequest.entity.SongRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {
    List<SongRequest> findBySessionIdOrderByRequestedAtAsc(Long sessionId);
}