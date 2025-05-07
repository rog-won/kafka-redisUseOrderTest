package com.example.kafkaorder.security;

import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:default-secret-key-for-jwt-please-change-in-production}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24시간
    private int jwtExpirationMs;
    
    private final UserRepository userRepository;
    
    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        // 사용자 권한 정보 수집
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
                
        return generateTokenWithAuthorities(userPrincipal.getUsername(), authorities);
    }

    public String generateTokenFromUsername(String username) {
        // 사용자 정보에서 역할 조회
        String authorities = userRepository.findByUsername(username)
                .map(User::getRole)
                .orElse("ROLE_USER");
                
        return generateTokenWithAuthorities(username, authorities);
    }
    
    private String generateTokenWithAuthorities(String username, String authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .claim("auth", authorities)  // 권한 정보 추가
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    
    public String getAuthoritiesFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("auth", String.class);
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
} 