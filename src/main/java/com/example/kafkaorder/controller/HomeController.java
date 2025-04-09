package com.example.kafkaorder.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 루트 경로로 접속 시 JWT 토큰 여부에 따라 로그인 페이지 또는 인덱스 페이지로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        // 서버 측에서는 리다이렉트만 처리하고, 클라이언트 측(JavaScript)에서 토큰 확인 후 적절한 페이지로 이동
        return "redirect:/index";
    }
    
    /**
     * 인덱스 페이지 접근
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
} 