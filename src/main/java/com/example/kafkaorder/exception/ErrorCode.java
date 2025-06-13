package com.example.kafkaorder.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션에서 사용하는 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 타입의 값입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C004", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C005", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다."),
    
    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U004", "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U005", "비밀번호가 올바르지 않습니다."),
    
    // 제품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "제품을 찾을 수 없습니다."),
    PRODUCT_CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "P002", "이미 존재하는 제품 코드입니다."),
    
    // 창고 관련 에러
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
    WAREHOUSE_CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "W002", "이미 존재하는 창고 코드입니다."),
    
    // 재고 관련 에러
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고 정보를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "I002", "재고가 부족합니다."),
    
    // 주문 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "O002", "이미 처리된 주문입니다."),
    ORDER_CANNOT_BE_CANCELED(HttpStatus.BAD_REQUEST, "O003", "취소할 수 없는 주문입니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
} 