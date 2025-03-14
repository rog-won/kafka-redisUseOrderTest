package com.example.kafkaorder.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryId implements java.io.Serializable {
    private String productId;
    private String warehouseId;
}