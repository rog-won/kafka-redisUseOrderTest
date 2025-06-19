package com.example.kafkaorder.controller;

import com.example.kafkaorder.exception.BusinessException;
import com.example.kafkaorder.exception.ErrorCode;
import com.example.kafkaorder.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 에러 로깅 테스트용 컨트롤러 (개발 환경 전용)
 */
@Slf4j
@RestController
@Profile({"dev", "test"})
@RequestMapping("/api/test/errors")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class ErrorTestController {

    /**
     * 비즈니스 예외 테스트
     */
    @GetMapping("/business")
    public String testBusinessException() {
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "테스트용 비즈니스 예외입니다");
    }

    /**
     * 리소스 찾기 실패 예외 테스트
     */
    @GetMapping("/not-found")
    public String testResourceNotFoundException() {
        throw new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "테스트용 리소스 찾기 실패 예외입니다");
    }

    /**
     * 런타임 예외 테스트
     */
    @GetMapping("/runtime")
    public String testRuntimeException() {
        throw new RuntimeException("테스트용 런타임 예외입니다");
    }

    /**
     * NullPointerException 테스트
     */
    @GetMapping("/null-pointer")
    public String testNullPointerException() {
        String test = null;
        return test.toString(); // NullPointerException 발생
    }

    /**
     * IllegalArgumentException 테스트
     */
    @GetMapping("/illegal-argument")
    public String testIllegalArgumentException() {
        throw new IllegalArgumentException("테스트용 잘못된 인수 예외입니다");
    }
} 