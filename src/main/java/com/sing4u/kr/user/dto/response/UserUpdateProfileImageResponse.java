package com.sing4u.kr.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateProfileImageResponse {
    @Schema(description = "성공적으로 업로드 및 변경된 프로필 이미지의 URL", example = "https://s3.ap-northeast-2.amazonaws.com/your-bucket/user/1/profile/image.jpg")
    private String profileImage;

    public static UserUpdateProfileImageResponse of(String profileImage) {
        return UserUpdateProfileImageResponse.builder()
                .profileImage(profileImage)
                .build();
    }
}
