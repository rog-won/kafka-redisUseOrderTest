package com.example.kafkaorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String name;
    private String role;
    
    public LoginResponse(String token, String username, String name) {
        this.token = token;
        this.username = username;
        this.name = name;
    }
} 