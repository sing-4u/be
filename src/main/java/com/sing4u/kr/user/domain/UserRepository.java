package com.sing4u.kr.user.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    List<User> getUsers();
    User save(User user);
    User updateProfile(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    void deleteById(User user);
}
