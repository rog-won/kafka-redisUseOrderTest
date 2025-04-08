package com.example.kafkaorder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/auth")
public class AuthViewController {

    @GetMapping("/login")
    public String login() {
        return "view/auth/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "view/auth/signup";
    }
} 