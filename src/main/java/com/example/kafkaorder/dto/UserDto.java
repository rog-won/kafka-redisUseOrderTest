package com.example.kafkaorder.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserDto {
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하여야 합니다")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    private String role = "ROLE_USER"; // 기본 역할
} 