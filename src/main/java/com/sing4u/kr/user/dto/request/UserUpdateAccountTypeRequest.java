package com.sing4u.kr.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import com.sing4u.kr.user.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateAccountTypeRequest {

    @Schema(description = "변경할 계정 유형", example = "ARTIST", allowableValues = {"USER", "ARTIST"},implementation = UserType.class)
    @NotNull(message = "계정 유형은 필수입니다.")
    private UserType userType;
}
