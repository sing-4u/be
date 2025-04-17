package com.sing4u.kr.user.service;

import com.sing4u.kr.user.dto.request.UserCreateRequest;
import com.sing4u.kr.user.dto.request.UserUpdateRequest;
import com.sing4u.kr.user.dto.response.UserCreateResponse;
import com.sing4u.kr.user.dto.response.UserDetailResponse;
import com.sing4u.kr.user.dto.response.UserListResponse;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.reopository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {
        User user = User.of(request.getEmail(), request.getNickname(), passwordEncoder.encode(request.getPassword()), request.getAccountType());
        return UserCreateResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Slice<UserListResponse> getUserListSearch(String keyword, Pageable pageable) {
        return userRepository.searchByNickname(keyword, pageable)
                .map(UserListResponse::from);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDetailResponse::from).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserDetailResponse updateNickname(Long id, UserUpdateRequest request) {
        User user = getEntityOrThrow(id);
        user.updateNickname(request.getNickname());
        return UserDetailResponse.from(user);
    }

    @Transactional
    public UserDetailResponse updateEmail(Long id, UserUpdateRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updateEmail(request.getEmail());
        return UserDetailResponse.from(user);
    }

    @Transactional
    public UserDetailResponse updatePassword(Long id, UserUpdateRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        return UserDetailResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.softDeleteById(id, LocalDateTime.now());
    }

    private User getEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
