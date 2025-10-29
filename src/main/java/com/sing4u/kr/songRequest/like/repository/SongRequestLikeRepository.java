package com.sing4u.kr.songRequest.like.repository;

import com.sing4u.kr.songRequest.like.entity.SongRequestLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRequestLikeRepository extends JpaRepository<SongRequestLike, Long> {

    boolean existsByUserIdAndSongRequestId(Long userId, Long songRequestId);

    Optional<SongRequestLike> findByUserIdAndSongRequestId(Long userId, Long songRequestId);

}
