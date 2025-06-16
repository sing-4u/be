package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class UserCreateRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "사용자 닉네임", example = "테스트유저")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "비밀번호", example = "password1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "계정 유형", example = "USER", allowableValues = {"USER", "ARTIST"})
    @NotNull(message = "계정 유형은 필수입니다.")
    private UserType userType;
}
