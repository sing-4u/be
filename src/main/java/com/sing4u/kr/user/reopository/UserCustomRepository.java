package com.sing4u.kr.user.reopository;

import com.sing4u.kr.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserCustomRepository {
    Slice<User> searchByNickname(String keyword, Pageable pageable);
}
