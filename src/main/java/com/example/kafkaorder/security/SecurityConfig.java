package com.example.kafkaorder.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityDebugFilter securityDebugFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                         SecurityDebugFilter securityDebugFilter,
                         CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityDebugFilter = securityDebugFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정 활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/**")
                .ignoringRequestMatchers("/users/api/**")
                .ignoringRequestMatchers("/css/**", "/js/**", "/images/**")
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 인증 API 및 로그인/회원가입 페이지 허용
                .requestMatchers("/api/auth/**", "/", "/index", "/view/auth/**").permitAll()
                // 정적 리소스 접근 허용
                .requestMatchers("/css/**", "/js/**", "/images/**", "/product/image/**").permitAll()
                // 사용자 관리 페이지는 ROLE_SUPER_ADMIN 권한 필요
                .requestMatchers("/users", "/users/**").hasAuthority("ROLE_SUPER_ADMIN")
                // 현재는 모든 기능 페이지 접근 허용 (추후 인증 필요로 변경 가능)
                .requestMatchers(
                        "/product/**",
                        "/warehouses/**",
                        "/inventory/**",
                        "/orders/**")
                    .permitAll()
                .anyRequest().authenticated()
            );

        // 디버그 필터 추가 (가장 앞에)
        http.addFilterBefore(securityDebugFilter, UsernamePasswordAuthenticationFilter.class);
        
        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 