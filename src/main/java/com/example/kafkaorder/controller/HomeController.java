package com.example.kafkaorder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 루트 경로로 접속 시 로그인 페이지로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/view/auth/login";
    }
    
    /**
     * 인덱스 페이지 접근
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
} 