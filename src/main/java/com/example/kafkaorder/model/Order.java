package com.example.kafkaorder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders") // 테이블 이름 지정 (필요에 따라 변경)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String orderId; // Kafka에서 전달된 주문 ID

    // 제품에 대한 연관관계 (제품은 반드시 존재해야 함)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;
    // 주문 상태: 예) CREATED, ACCEPTED, REJECTED 등
    private String status;
}