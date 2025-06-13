package com.example.kafkaorder.exception;

/**
 * 잘못된 요청 데이터로 인한 예외
 */
public class InvalidRequestException extends BusinessException {
    
    public InvalidRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }
    
    public InvalidRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 