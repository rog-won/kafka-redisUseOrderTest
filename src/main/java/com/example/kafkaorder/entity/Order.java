package com.example.kafkaorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문 코드는 고유하며, "OD" + YYMMDD + 랜덤 4자리로 생성됨
    @Column(unique = true, nullable = false)
    private String code;

    // 등록된 제품과 연관관계
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 주문 수량
    @Column(nullable = false)
    private int quantity;

    // 주문 상태 (예: CREATED, ACCEPTED, CANCELED 등)
    @Column(nullable = false)
    private String status;

    // 주문 시 선택한 창고의 코드 (예: "ON", "OFF" 등)
    @ManyToOne(optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    // 주문 등록 시간
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        // 주문 코드 생성: "OD" + YYMMDD + 랜덤 4자리
        if (code == null || code.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            String datePart = LocalDateTime.now().format(formatter);
            int randomNumber = ThreadLocalRandom.current().nextInt(0, 10000); // 0 ~ 9999
            code = "OD" + datePart + String.format("%04d", randomNumber);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
