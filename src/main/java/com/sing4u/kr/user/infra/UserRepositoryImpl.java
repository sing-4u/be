package com.sing4u.kr.user.infra;

import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpa;

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User updateProfile(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteById(User user) {
        jpa.deleteById(user.getId());
    }
}
