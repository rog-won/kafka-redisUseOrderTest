package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import lombok.Data;

@Data
public class InventoryDto {
    // 폼에서 선택한 제품의 코드와 창고의 코드를 받음
    private String productCode;
    private String warehouseCode;
    private int quantity;

    /**
     * Product와 Warehouse 객체를 받아 Inventory 엔티티로 변환
     */
    public Inventory toEntity(Product product, Warehouse warehouse) {
        return Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(this.quantity)
                .build();
    }
}
