package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.UserDto;
import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.exception.BusinessException;
import com.example.kafkaorder.exception.ErrorCode;
import com.example.kafkaorder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 사용 가능한 역할 목록
    private static final List<String> AVAILABLE_ROLES = Arrays.asList(
            "ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_USER"
    );

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserDto userDto) {
        // 아이디(username) 중복 검사
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 역할 검증 및 기본값 설정
        String role = userDto.getRole();
        if (role == null || role.isEmpty() || !AVAILABLE_ROLES.contains(role)) {
            role = "ROLE_USER"; // 기본 역할
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .email(userDto.getEmail())
                .role(role)
                .build();

        // 저장 및 반환
        return userRepository.save(user);
    }
    
    /**
     * 사용자 ID로 사용자 역할을 조회합니다.
     */
    public String getUserRole(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getRole).orElse(null);
    }
    
    /**
     * 사용자명으로 사용자 역할을 조회합니다.
     */
    public String getUserRole(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getRole).orElse(null);
    }
    
    /**
     * 사용자 역할을 업데이트합니다.
     * 슈퍼관리자만 다른 사용자의 역할을 변경할 수 있습니다.
     */
    @Transactional
    public boolean updateUserRole(String username, String newRole, String currentUserRole) {
        // 권한 검증: 슈퍼 관리자만 역할 변경 가능
        if (!"ROLE_SUPER_ADMIN".equals(currentUserRole)) {
            return false;
        }
        
        // 역할 유효성 검증
        if (!AVAILABLE_ROLES.contains(newRole)) {
            return false;
        }
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(newRole);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * 사용 가능한 역할 목록을 반환합니다.
     */
    public List<String> getAvailableRoles() {
        return AVAILABLE_ROLES;
    }
    
    /**
     * 모든 사용자 목록을 반환합니다.
     * 슈퍼 관리자용 기능입니다.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
} 