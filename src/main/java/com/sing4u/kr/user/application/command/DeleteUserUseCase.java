package com.sing4u.kr.user.application.command;

import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import com.sing4u.kr.user.domain.RetiredEmail;
import com.sing4u.kr.user.infra.RetiredEmailJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final OldUserRepository userRepository;
    private final RetiredEmailJpaRepository retiredEmailJpaRepository;

    @Transactional
    public void execute(UUID userId) {
        OldUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        userRepository.deleteById(user);

        retiredEmailJpaRepository.save(new RetiredEmail(user.getEmail()));
    }

}
