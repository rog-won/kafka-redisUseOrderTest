package com.example.kafkaorder.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String productId;
    private String warehouseId;
    private int quantity;

    // "INBOUND" 또는 "OUTBOUND"
    private String type;

    private LocalDateTime transactionTime;

    // 주문과 연관된 경우 해당 주문 ID 기록 (옵션)
    private String orderId;
}