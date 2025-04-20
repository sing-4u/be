package com.sing4u.kr.user.service;

import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.reopository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UserProfileResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserProfileResponse::from).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateAccountType(Long id, UserUpdateAccountTypeRequest request) {
        User user = getEntityOrThrow(id);
        user.updateAccountType(request.getAccountType());
        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long id, UserUpdateProfileRequest request) {
        User user = getEntityOrThrow(id);
        user.updateProfile(
                request.getProfileImage(),
                request.getIntroduction(),
                request.getMainCoverUrl(),
                request.getActivityPlatformUrl(),
                request.getActivityPlatformType()
        );
        userRepository.save(user);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserUpdateEmailResponse updateEmail(Long id, UserUpdateEmailRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updateEmail(request.getEmail());
        userRepository.save(user);
        return UserUpdateEmailResponse.from(user);
    }

    @Transactional
    public UserUpdatePasswordResponse updatePassword(Long id, UserUpdatePasswordRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return UserUpdatePasswordResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getEntityOrThrow(id);
        user.updateDeletedAt();
        userRepository.save(user);
    }

    private User getEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
