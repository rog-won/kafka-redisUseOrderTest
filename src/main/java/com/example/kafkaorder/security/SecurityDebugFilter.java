package com.example.kafkaorder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // 정적 리소스와 로그인 요청은 로깅하지 않음
        if (!requestURI.startsWith("/css/") && !requestURI.startsWith("/js/") && 
            !requestURI.startsWith("/images/") && !requestURI.equals("/api/auth/login")) {
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            System.out.println("====== 요청 디버그 정보 ======");
            System.out.println("요청 URI: " + requestURI);
            System.out.println("인증 정보: " + (auth != null ? "있음" : "없음"));
            
            if (auth != null) {
                System.out.println("인증된 사용자: " + auth.getName());
                System.out.println("권한 목록: " + auth.getAuthorities());
                System.out.println("인증 객체 타입: " + auth.getClass().getName());
                System.out.println("인증 세부정보: " + auth.getDetails());
            }
            
            System.out.println("==============================");
        }
        
        filterChain.doFilter(request, response);
    }
} 