package com.example.kafkaorder.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "아이디는 필수입니다")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
} 