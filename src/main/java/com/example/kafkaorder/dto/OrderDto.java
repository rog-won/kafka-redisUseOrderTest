package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderDto {
    @NotBlank(message = "제품 코드는 필수입니다")
    private String productCode;
    
    @NotBlank(message = "창고 코드는 필수입니다")
    private String warehouseCode;
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    private Integer quantity;
    
    @NotBlank(message = "등록자는 필수입니다")
    private String createdBy;

    /**
     * 실제 Product 객체를 받아 Order 엔티티로 변환합니다.
     * 주문 상태는 기본적으로 "CREATED"로 설정됩니다.
     * 제품과 창고 정보를 백업하여 나중에 제품이나 창고가 삭제되더라도 주문 정보는 유지됩니다.
     */
    public Order toEntity(Product product, Warehouse warehouse) {
        Order order = Order.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(this.quantity)
                .status("CREATED")
                .createdBy(this.createdBy)
                .build();
        
        // 제품과 창고 정보 백업
        if (product != null) {
            order.setProductCode(product.getCode());
            order.setProductName(product.getName());
        }
        
        if (warehouse != null) {
            order.setWarehouseCode(warehouse.getCode());
            order.setWarehouseName(warehouse.getName());
        }
        
        return order;
    }
}
