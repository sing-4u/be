package com.sing4u.kr.user.infra;

import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements OldUserRepository {
    private final UserJpaRepository jpa;

    @Override
    public List<OldUser> getUsers() {
        return List.of();
    }

    @Override
    public OldUser save(OldUser user) {
        return null;
    }

    @Override
    public OldUser updateProfile(OldUser user) {
        return null;
    }

    @Override
    public Optional<OldUser> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<OldUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteById(OldUser user) {
        jpa.deleteById(user.getId());
    }

    @Override
    public Boolean existsByEmail(String email) {
        return null;
    }
}
