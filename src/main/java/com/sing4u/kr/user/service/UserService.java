package com.sing4u.kr.user.service;

import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.home.dto.request.HomeRequest;
import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.entity.UserActivityPlatform;
import com.sing4u.kr.user.repository.UserActivityPlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserActivityPlatformRepository userActivityPlatformRepository;
    private final PasswordEncoder passwordEncoder;
    @Cacheable(
            value = "homeArtists",
            key = "'page=' + #page + ',seed=' + #seed",
            condition = "#keyword == null")
    @Transactional(readOnly = true)
    public List<UserListResponse> getArtistList(HomeRequest request) {
        int offset = request.getPage() * request.getPageSize();
        List<User> userList = userRepository.findArtistsWithKeywordAndRandomOrder(request.getKeyword(), request.getSeed(), offset, request.getPageSize());
        return UserListResponse.fromList(userList);
    }

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {
        User user = User.of(request.getNickname(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getUserType());
        return UserCreateResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        User user = getEntityOrThrow(id);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public void updateAccountType(Long id, UserUpdateAccountTypeRequest request) {
        User user = getEntityOrThrow(id);
        user.updateAccountType(request.getUserType());
        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long id, UserUpdateProfileRequest request) {
        User user = getEntityOrThrow(id);
        user.updateProfile(
                request.getProfileImage(),
                request.getNickname(),
                request.getIntroduction(),
                request.getMainCoverUrl()
        );
        userRepository.save(user);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserActivityPlatformResponse getActivityPlatform(Long id) {
        List<UserActivityPlatform> platforms = userActivityPlatformRepository.findAllByUserId(id);
        return UserActivityPlatformResponse.from(platforms);
    }

    @Transactional
    public UserActivityPlatformResponse updateActivityPlatform(Long id, UserUpdateActivityPlatformRequest request) {
        List<UserActivityPlatform> existingPlatformList = userActivityPlatformRepository.findAllByUserId(id);
        Map<String, UserActivityPlatform> existingPlatformMap = existingPlatformList.stream()
                .collect(Collectors.toMap(UserActivityPlatform::getActivityPlatformUrl, Function.identity()));
        List<UserActivityPlatform> platformsToSave = request.getActivityPlatforms().stream()
                .map(requestPlatform -> existingPlatformMap.getOrDefault(
                        requestPlatform.getPlatformUrl(),
                        UserActivityPlatform.of(requestPlatform.getPlatformType(), requestPlatform.getPlatformUrl(), id)
                ))
                .toList();
        Set<String> requestedPlatformUrls = request.getActivityPlatforms().stream()
                .map(ActivityPlatformRequest::getPlatformUrl)
                .collect(Collectors.toSet());
        List<UserActivityPlatform> platformsToDelete = existingPlatformList.stream()
                .filter(existing -> !requestedPlatformUrls.contains(existing.getActivityPlatformUrl()))
                .toList();
        if (!platformsToDelete.isEmpty()) {
            userActivityPlatformRepository.deleteAll(platformsToDelete);
        }
        userActivityPlatformRepository.saveAll(platformsToSave);
        return UserActivityPlatformResponse.from(platformsToSave);
    }

    @Transactional
    public UserUpdateEmailResponse updateEmail(Long id, UserUpdateEmailRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.", ResponseCode.ERROR_WRONG_PARAMETERS);
        }
        user.updateEmail(request.getEmail());
        userRepository.save(user);
        return UserUpdateEmailResponse.from(user);
    }

    @Transactional
    public UserUpdatePasswordResponse updatePassword(Long id, UserUpdatePasswordRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.", ResponseCode.ERROR_WRONG_PARAMETERS);
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return UserUpdatePasswordResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getEntityOrThrow(id);
        user.delete();
        userRepository.save(user);
    }

    private User getEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다.", ResponseCode.ERROR_NO_DATA));
    }
}
