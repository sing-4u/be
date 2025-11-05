package com.sing4u.kr.user.service;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.Exception409;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.file.service.S3Service;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.home.dto.request.HomeRequest;
import com.sing4u.kr.session.service.SessionService;
import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.entity.UserActivityPlatform;
import com.sing4u.kr.user.repository.UserActivityPlatformRepository;
import com.sing4u.kr.user.utils.UserFileUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3Service s3Service;
    private final SessionService sessionService;

//    @Cacheable(
//            value = "homeArtists",
//            key = "'page=' + #request.page + ',size=' + #request.pageSize",
//            condition = "#request.keyword == null || #request.keyword.trim().isEmpty()"
//    )
    @Transactional(readOnly = true)
    public PagingResponse<UserListResponse> getArtistList(HomeRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        Slice<User> userSlice = userRepository.findArtistsWithKeywordAndRandomOrder(request.getKeyword(), request.getSeed(), pageable);

        Slice<UserListResponse> responseSlice = userSlice.map(UserListResponse::from);
        return PagingResponse.of(responseSlice);
    }

//    @CacheEvict(
//            value = "homeArtists",
//            allEntries = true,
//            condition = "#request.userType == T(com.sing4u.kr.user.entity.enums.UserType).ARTIST"
//    )
    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new Exception409(ResponseCode.ERROR_ALREADY_EXIST_USER, "이미 사용 중인 이메일입니다.");
        }

        User user = User.of(request.getNickname(), email, passwordEncoder.encode(request.getPassword()), request.getUserType());
        return UserCreateResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        User user = getEntityOrThrow(id);
        return UserProfileResponse.from(user);
    }

    @CacheEvict(value = "homeArtists", allEntries = true)
    @Transactional
    public void updateAccountType(Long id, UserUpdateAccountTypeRequest request) {
        User user = getEntityOrThrow(id);
        user.updateAccountType(request.getUserType());
        userRepository.save(user);
    }

    //@CacheEvict(value = "homeArtists", allEntries = true)
    @Transactional
    public UserProfileResponse updateProfile(Long id, UserUpdateProfileRequest request) {
        User user = getEntityOrThrow(id);
        user.updateProfile(
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

        //소셜 로그인 계정 차단
        if (!user.getSocialType().equals(SocialType.LOCAL)) {
            throw new Exception409(ResponseCode.PASSWORD_RESET_SOCIAL_ACCOUNT, "소셜 로그인 계정은 이메일을 변경할 수 없습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.", ResponseCode.ERROR_PASSWORD_MISMATCH);
        }

        //이메일 검증
        String newEmail = request.getNewEmail();
        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(newEmail)) {
            throw new Exception400("현재 이메일과 동일합니다.", ResponseCode.ERROR_DATA_EXISTED);
        }
        if (userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new Exception409(ResponseCode.ERROR_ALREADY_EXIST_USER, "이미 사용 중인 이메일입니다.");
        }

        user.updateEmail(newEmail);
        userRepository.save(user);
        return UserUpdateEmailResponse.from(user);
    }

    @Transactional
    public void updatePassword(Long id, UserUpdatePasswordRequest request) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.", ResponseCode.ERROR_PASSWORD_MISMATCH);
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        //return UserUpdatePasswordResponse.from(user);
    }

    //@CacheEvict(value = "homeArtists", allEntries = true)
    @Transactional
    public void deleteUser(Long id) {
        User user = getEntityOrThrow(id);

        // 아티스트라면 열린 세션 정리
        if (user.getUserType() == UserType.ARTIST) {
            sessionService.closeAllOpenByArtist(user.getId());
        }

        user.delete();
        userRepository.save(user);
    }

    private User getEntityOrThrow(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다.", ResponseCode.ERROR_NO_DATA));
    }

    //@CacheEvict(value = "homeArtists", allEntries = true)
    @Transactional
    public UserUpdateProfileImageResponse updateUserProfileImage(Long id, MultipartFile file) {
        User user = this.getEntityOrThrow(id);

        String uploadPath = UserFileUtils.getProfileImageKey(id);
        String uploadedPath = this.s3Service.uploadFile(uploadPath, file);

        user.updateProfileImage(uploadedPath);

        this.userRepository.save(user);

        return UserUpdateProfileImageResponse.of(user.getProfileImage());
    }

    @Transactional(readOnly = true)
    public Long getUserIdByPublicId(String publicId) {
        User user = userRepository.findByUserPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + publicId));

        return user.getId();
    }

    //@CacheEvict(value = "homeArtists", allEntries = true)
    @Transactional
    public void deleteUserProfileImage(Long id) {
        User user = this.getEntityOrThrow(id);

        String imageUrl = user.getProfileImage();
        if (imageUrl != null) {
            s3Service.deleteFile(imageUrl); // 전체 URL 넘겨도 내부에서 key 추출
        }

        user.setProfileImage(null);
        //userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserPublicProfile(String publicId) {
        User user = userRepository.findByUserPublicIdAndUserTypeAndDeletedAtIsNull(publicId, UserType.ARTIST)
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다.", ResponseCode.ERROR_NO_DATA));

        return UserProfileResponse.from(user);
    }

}
