package com.example.kafkaorder.exception;

/**
 * 재고 부족으로 인한 예외
 */
public class InsufficientStockException extends BusinessException {
    
    public InsufficientStockException(String productCode, int currentStock, int requiredQuantity) {
        super(ErrorCode.INSUFFICIENT_STOCK, 
              String.format("재고 부족: 제품 '%s', 현재 재고: %d, 필요 수량: %d", 
                          productCode, currentStock, requiredQuantity));
    }
    
    public InsufficientStockException(String message) {
        super(ErrorCode.INSUFFICIENT_STOCK, message);
    }
} 