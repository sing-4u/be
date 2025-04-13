package com.sing4u.kr.user.service;

import com.sing4u.kr.user.dto.UserDto;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.reopository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = User.of(dto.getEmail(), dto.getNickname(), passwordEncoder.encode(dto.getPassword()));
        return toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto);
    }

    @Transactional
    public UserDto updateNickname(Long id, UserDto dto) {
        User user = getEntityOrThrow(id);
        user.updateNickname(dto.getNickname());
        return toDto(user);
    }

    @Transactional
    public UserDto updateEmail(Long id, UserDto dto) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updateEmail(dto.getEmail());
        return toDto(user);
    }

    @Transactional
    public UserDto updatePassword(Long id, UserDto dto) {
        User user = getEntityOrThrow(id);
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
        return toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getEntityOrThrow(id);
        userRepository.delete(user);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    private User getEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
