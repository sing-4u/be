package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateEmailRequest {

    @Schema(description = "변경할 새 이메일", example = "new_email@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "사용자 닉네임 (현재 로직에선 미사용)", example = "테스트유저")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "현재 비밀번호 (본인 확인용)", example = "password1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    //@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "소셜 타입 (현재 로직에선 미사용)", example = "LOCAL", allowableValues = {"GOOGLE", "LOCAL"}, implementation = SocialType.class)
    private SocialType socialType;
}
