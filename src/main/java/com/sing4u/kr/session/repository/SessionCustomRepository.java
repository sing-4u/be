package com.sing4u.kr.session.repository;

import com.sing4u.kr.session.entity.Session;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionCustomRepository  {
    List<Session> findAllWithSongsByArtist(@Param("artistId") Long artistId);
}
