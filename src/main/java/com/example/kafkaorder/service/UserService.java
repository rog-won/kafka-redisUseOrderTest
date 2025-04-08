package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.UserDto;
import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserDto userDto) {
        // 아이디(username) 중복 검사
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호는 나중에 암호화
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        // 저장 및 반환
        return userRepository.save(user);
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
} 