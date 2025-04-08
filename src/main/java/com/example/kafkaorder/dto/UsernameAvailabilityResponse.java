package com.example.kafkaorder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsernameAvailabilityResponse {
    private boolean available;
    private String message;
} 