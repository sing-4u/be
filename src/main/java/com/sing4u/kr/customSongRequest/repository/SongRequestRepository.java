package com.sing4u.kr.customSongRequest.repository;

import com.sing4u.kr.customSongRequest.entity.SongRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {
}