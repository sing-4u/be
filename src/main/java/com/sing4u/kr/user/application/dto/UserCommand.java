package com.sing4u.kr.user.application.dto;

import com.sing4u.kr.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record UserCommand(
        UUID id,
        String email,
        String password,
        UserRole userRole
) {
}
