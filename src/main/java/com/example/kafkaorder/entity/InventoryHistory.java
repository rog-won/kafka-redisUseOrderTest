package com.example.kafkaorder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "inventory_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제품 정보
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 창고 정보
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    // 수량 변경 (양수: 입고, 음수: 출고)
    @Column(nullable = false)
    private int quantityChange;

    // 등록자 정보
    @Column(length = 50)
    private String registeredBy;

    // 작업 유형 (입고, 출고, 조정 등)
    @Column(length = 20)
    private String actionType;

    // 메모 (필요 시)
    @Column(length = 255)
    private String notes;

    // 생성 시간
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 