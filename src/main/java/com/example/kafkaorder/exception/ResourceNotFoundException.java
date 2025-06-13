package com.example.kafkaorder.exception;

/**
 * 리소스를 찾을 수 없는 경우 발생하는 예외
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCode.ENTITY_NOT_FOUND, 
              String.format("%s를 찾을 수 없습니다. %s: %s", resourceName, fieldName, fieldValue));
    }
    
    public ResourceNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }
    
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 