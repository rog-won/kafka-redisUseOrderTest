package com.example.kafkaorder.config;

import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    /**
     * 애플리케이션 시작 시 초기 데이터를 생성합니다.
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 슈퍼 어드민 계정이 없으면 생성
            if (!userRepository.existsByUsername("superadmin")) {
                User superAdmin = User.builder()
                        .username("superadmin")
                        .password(passwordEncoder.encode("superadmin123"))
                        .name("슈퍼관리자")
                        .email("superadmin@example.com")
                        .role("ROLE_SUPER_ADMIN")
                        .build();
                userRepository.save(superAdmin);
                System.out.println("슈퍼관리자 계정이 생성되었습니다. (superadmin / superadmin123)");
            } else {
                // 기존 슈퍼어드민 계정의 역할 확인 및 업데이트
                Optional<User> existingUser = userRepository.findByUsername("superadmin");
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    if (!user.getRole().equals("ROLE_SUPER_ADMIN")) {
                        user.setRole("ROLE_SUPER_ADMIN");
                        userRepository.save(user);
                        System.out.println("기존 슈퍼어드민 계정의 역할이 업데이트되었습니다: " + user.getRole());
                    } else {
                        System.out.println("기존 슈퍼어드민 계정 확인: " + user.getUsername() + ", 역할: " + user.getRole());
                    }
                }
            }
            
            // 일반 관리자 계정이 없으면 생성
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .name("관리자")
                        .email("admin@example.com")
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);
                System.out.println("관리자 계정이 생성되었습니다. (admin / admin123)");
            }
            
            // 일반 사용자 계정이 없으면 생성
            if (!userRepository.existsByUsername("user")) {
                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .name("일반사용자")
                        .email("user@example.com")
                        .role("ROLE_USER")
                        .build();
                userRepository.save(user);
                System.out.println("일반 사용자 계정이 생성되었습니다. (user / user123)");
            }
            
            // 데이터베이스의 모든 사용자 출력 (디버깅)
            System.out.println("\n===== 현재 데이터베이스 사용자 목록 =====");
            userRepository.findAll().forEach(u -> 
                System.out.println("사용자: " + u.getUsername() + ", 역할: " + u.getRole())
            );
            System.out.println("====================================\n");
        };
    }
} 