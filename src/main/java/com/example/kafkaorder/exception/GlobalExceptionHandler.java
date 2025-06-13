package com.example.kafkaorder.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("비즈니스 예외 발생 - Code: {}, Message: {}, Path: {}", 
                e.getErrorCode().getCode(), e.getMessage(), request.getRequestURI());
                
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getErrorCode().getStatus().value())
                .error(e.getErrorCode().getStatus().getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
    }

    /**
     * 리소스를 찾을 수 없는 경우 처리
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 인증 실패 처리
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("인증에 실패했습니다. 아이디와 비밀번호를 확인해주세요.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 권한 부족 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("접근 권한이 없습니다.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 잘못된 요청 데이터 처리
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 재고 부족 예외 처리
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException e, HttpServletRequest request) {
        log.warn("재고 부족 예외 발생 - Message: {}, Path: {}", e.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * @RequestBody 및 @Valid 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("검증 실패 - Path: {}, Errors: {}", request.getRequestURI(), e.getBindingResult().getAllErrors().size());
        
        StringBuilder errorMessage = new StringBuilder("입력값 검증에 실패했습니다: ");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(String.format("[%s: %s] ", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage.toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ModelAttribute 및 폼 데이터 검증 실패 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("폼 데이터 검증 실패 - Path: {}, Errors: {}", request.getRequestURI(), e.getBindingResult().getAllErrors().size());
        
        StringBuilder errorMessage = new StringBuilder("폼 데이터 검증에 실패했습니다: ");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(String.format("[%s: %s] ", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage.toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("런타임 예외 발생 - Message: {}, Path: {}", e.getMessage(), request.getRequestURI(), e);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생 - Type: {}, Message: {}, Path: {}", 
                e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), e);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("서버 내부 오류가 발생했습니다.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 웹 페이지 요청에 대한 예외 처리 (HTML 응답)
     * API 요청이 아닌 웹 페이지 요청에서 발생한 예외를 처리합니다.
     */
    @ExceptionHandler({BusinessException.class, ResourceNotFoundException.class})
    public ModelAndView handleWebException(Exception e, HttpServletRequest request) {
        // API 요청인지 확인 (Content-Type이나 Accept 헤더로 판단)
        String accept = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        
        if ((accept != null && accept.contains("application/json")) ||
            (contentType != null && contentType.contains("application/json")) ||
            request.getRequestURI().startsWith("/api/")) {
            // API 요청이면 JSON 응답 처리를 위해 다시 throw
            throw new RuntimeException(e);
        }

        // 웹 페이지 요청이면 에러 페이지로 리다이렉트
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/error");
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        
        return modelAndView;
    }
} 