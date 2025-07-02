package com.sing4u.kr.user.service;

import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.file.service.S3Service;
import com.sing4u.kr.user.dto.request.UserCreateRequest;
import com.sing4u.kr.user.dto.response.UserCreateResponse;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserActivityPlatformRepository;
import com.sing4u.kr.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserActivityPlatformRepository userActivityPlatformRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service; // S3Service도 Mock으로 만듭니다.

    @InjectMocks
    private UserService userService;

    private User user;

    private static class TestUserCreateRequest extends UserCreateRequest {
        public TestUserCreateRequest(String email, String nickname, String password, UserType userType) {
            super(email, nickname, password, userType);
        }
    }

    @BeforeEach
    void setUp() {
        user = User.testUserBuilder(1L, "test-public-id","테스트유저", UserType.FAN);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setOpen(false);
        user.setDeletedAt(null);
    }

    @Nested
    @DisplayName("회원가입 (createUser)")
    class CreateUserTests {
        @Test
        @DisplayName("성공")
        void createUser_Success() {
            // given
            UserCreateRequest request =  new TestUserCreateRequest(
                    "new@example.com",
                    "새유저",
                    "password1234",
                    UserType.FAN
            );

            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User userToBeSaved = invocation.getArgument(0);
                User mockedSavedUser = User.testUserBuilder(
                        2L,
                        "test-public-id2",// DB에서 ID가 생성되었다고 가정
                        userToBeSaved.getNickname(),
                        userToBeSaved.getUserType()
                );

                // setter로 나머지 필드 설정
                mockedSavedUser.setEmail(userToBeSaved.getEmail());
                mockedSavedUser.setPassword(userToBeSaved.getPassword());
//                mockedSavedUser.setIsOpen(userToBeSaved.isOpen()); // User.of()에서 false로 설정됨
                mockedSavedUser.setCreatedAt(LocalDateTime.now()); // @CreationTimestamp 동작 흉내

                return mockedSavedUser;
            });

            // when
            UserCreateResponse response = userService.createUser(request);

            // then
            assertThat(response.getUserPublicId()).isEqualTo("test-public-id2");
            assertThat(response.getEmail()).isEqualTo(request.getEmail());
            assertThat(response.getNickname()).isEqualTo(request.getNickname());

            verify(passwordEncoder).encode("password1234");
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 조회 (getEntityOrThrow / getUserById)")
    class GetUserTests {
        @Test
        @DisplayName("성공 - ID로 사용자를 찾았을 때")
        void getUserById_Success() {
            // given
            // userRepository가 findByIdAndDeletedAtIsNull을 호출하면 Optional<User>를 반환하도록 설정
            when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

            // when
            var response = userService.getUserById(1L);

            // then
            assertThat(response.getUserPublicId()).isEqualTo("test-public-id");
            assertThat(response.getNickname()).isEqualTo(user.getNickname());
            verify(userRepository).findByIdAndDeletedAtIsNull(1L);
        }

        @Test
        @DisplayName("실패 - ID에 해당하는 사용자가 없거나 삭제되었을 때")
        void getUserById_Failure_NotFound() {
            // given
            when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(Exception400.class);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 (deleteUser)")
    class DeleteUserTests {
        @Test
        @DisplayName("성공 - 사용자의 deletedAt 필드가 설정됨")
        void deleteUser_Success_SoftDelete() {
            // given
            when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // when
            userService.deleteUser(1L);

            // then
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(savedUser.getDeletedAt()).isNotNull();
            assertThat(savedUser.isDeleted()).isTrue();
        }
    }
}