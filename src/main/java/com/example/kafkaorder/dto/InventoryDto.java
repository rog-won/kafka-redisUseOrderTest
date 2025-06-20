package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class InventoryDto {
    // 폼에서 선택한 제품의 코드와 창고의 코드를 받음
    @NotBlank(message = "제품 코드는 필수입니다")
    private String productCode;
    
    @NotBlank(message = "창고 코드는 필수입니다")
    private String warehouseCode;
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    private Integer quantity;
    
    @NotBlank(message = "등록자는 필수입니다")
    private String registeredBy;
    
    private String notes; // 비고는 선택사항

    /**
     * Product와 Warehouse 객체를 받아 Inventory 엔티티로 변환
     */
    public Inventory toEntity(Product product, Warehouse warehouse) {
        return Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(this.quantity)
                .registeredBy(this.registeredBy)
                .build();
    }
}
