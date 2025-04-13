package com.sing4u.kr.user.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OldUserRepository {
    List<OldUser> getUsers();
    OldUser save(OldUser user);
    OldUser updateProfile(OldUser user);
    Optional<OldUser> findById(UUID id);
    Optional<OldUser> findByEmail(String email);
    void deleteById(OldUser user);
    Boolean existsByEmail(String email);
}
