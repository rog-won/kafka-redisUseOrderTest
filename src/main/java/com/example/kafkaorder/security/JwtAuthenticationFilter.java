package com.example.kafkaorder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            String requestURI = request.getRequestURI();
            
            // 로그인 요청이 아닌 경우만 로깅
            if (!requestURI.equals("/api/auth/login")) {
                System.out.println("JWT 토큰 확인 (" + requestURI + "): " + (jwt != null ? "있음" : "없음"));
                if (jwt == null) {
                    // Authorization 헤더 출력
                    String authHeader = request.getHeader("Authorization");
                    System.out.println("Authorization 헤더: " + authHeader);
                }
            }

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                // 토큰에서 권한 정보 가져오기
                String authoritiesString = tokenProvider.getAuthoritiesFromToken(jwt);
                List<SimpleGrantedAuthority> authorities = 
                        Arrays.stream(authoritiesString.split(","))
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toList());
                
                // 사용자 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 인증 객체 생성 (토큰에서 가져온 권한 정보 사용)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 디버깅 로그
                logger.info("JWT 인증 성공: " + username + ", 권한: " + authoritiesString);
            } else if (jwt != null) {
                // 토큰이 있지만 유효하지 않은 경우
                logger.info("JWT 토큰이 유효하지 않음");
            }
        } catch (Exception ex) {
            logger.error("JWT 인증에 실패했습니다", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 JWT 토큰 확인
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            System.out.println("Authorization 헤더에서 JWT 찾음");
            return bearerToken.substring(7);
        }
        
        // 2. 쿠키에서 JWT 토큰 확인
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    System.out.println("쿠키에서 JWT 찾음: " + cookie.getName());
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
} 