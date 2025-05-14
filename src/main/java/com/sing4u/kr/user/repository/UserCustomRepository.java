package com.sing4u.kr.user.repository;

import com.sing4u.kr.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserCustomRepository {
    List<User> findArtistsWithKeywordAndRandomOrder(String keyword, long seed, int offset, int limit);
}
