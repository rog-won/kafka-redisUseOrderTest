package com.example.kafkaorder.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Lob
    private String description;

    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() {
        // 상품 코드 생성 (code가 비어있으면 생성)
        if (code == null || code.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            String datePart = LocalDateTime.now().format(formatter);
            int randomNumber = ThreadLocalRandom.current().nextInt(0, 100000);
            code = "P" + datePart + String.format("%05d", randomNumber);
        }
        // 등록 시간 설정
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}