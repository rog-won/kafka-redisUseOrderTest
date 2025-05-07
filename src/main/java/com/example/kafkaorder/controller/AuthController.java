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

import jakarta.servlet.http.HttpServletResponse;

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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            System.out.println("로그인 요청: " + loginRequest.getUsername());
            
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
            
            // 사용자 역할 정보 가져오기
            String userRole = userService.getUserRole(userDetails.getUsername());
            System.out.println("사용자 로그인 성공: " + userDetails.getUsername() + ", 역할: " + userRole);
            System.out.println("권한 정보: " + authentication.getAuthorities());
            
            // JWT 토큰을 쿠키에도 설정
            jakarta.servlet.http.Cookie jwtCookie = new jakarta.servlet.http.Cookie("jwt", jwt);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(true); // JavaScript에서 접근 불가
            jwtCookie.setMaxAge(24 * 60 * 60); // 24시간
            response.addCookie(jwtCookie);
            
            return ResponseEntity.ok(new LoginResponse(jwt, userDetails.getUsername(), userDetails.getUsername(), userRole));
        } catch (Exception e) {
            e.printStackTrace();
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