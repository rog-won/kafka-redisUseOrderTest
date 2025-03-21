package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import lombok.Data;

@Data
public class OrderDto {
    private String productCode;
    private String warehouseCode;
    private int quantity;

    /**
     * 실제 Product 객체를 받아 Order 엔티티로 변환합니다.
     * 주문 상태는 기본적으로 "CREATED"로 설정됩니다.
     */
    public Order toEntity(Product product, Warehouse warehouse) {
        return Order.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(this.quantity)
                .status("CREATED")
                .build();
    }
}
