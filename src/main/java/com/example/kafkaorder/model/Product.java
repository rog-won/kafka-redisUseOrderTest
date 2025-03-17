package com.example.kafkaorder.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String productId; // 예: "P001"

    private String name;
    private String description;

    @PrePersist
    public void generateId() {
        if (productId == null || productId.trim().isEmpty()) {
            // "P" + 11자리 숫자. 0부터 10^11 미만의 난수를 생성
            long randomNum = ThreadLocalRandom.current().nextLong(0, 100_000_000_000L);
            productId = "P" + String.format("%011d", randomNum);
        }
    }
}