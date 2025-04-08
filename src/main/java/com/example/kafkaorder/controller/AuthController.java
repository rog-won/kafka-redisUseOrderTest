package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.LoginRequest;
import com.example.kafkaorder.dto.LoginResponse;
import com.example.kafkaorder.dto.UserDto;
import com.example.kafkaorder.dto.UsernameAvailabilityResponse;
import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.security.JwtTokenProvider;
import com.example.kafkaorder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            User user = userService.registerUser(userDto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // 인증에 성공한 사용자 정보를 가져옴 (UserDetails에서 추출)
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            
            return ResponseEntity.ok(new LoginResponse(jwt, userDetails.getUsername(), userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인에 실패했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = !userService.checkUsernameExists(username);
        String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        return ResponseEntity.ok(new UsernameAvailabilityResponse(isAvailable, message));
    }
} 