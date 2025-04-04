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

    // 등록된 제품과 연관관계 - 제품이 삭제되어도 주문 기록은 유지
    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id", nullable = true, foreignKey = @ForeignKey(name = "fk_order_product", foreignKeyDefinition = "FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE SET NULL"))
    private Product product;
    
    // 삭제된 제품 정보를 보존하기 위한 필드들
    @Column(name = "product_code")
    private String productCode;
    
    @Column(name = "product_name")
    private String productName;

    // 주문 수량
    @Column(nullable = false)
    private int quantity;

    // 주문 상태 (예: CREATED, ACCEPTED, CANCELED 등)
    @Column(nullable = false)
    private String status;

    // 주문 시 선택한 창고 - 창고가 삭제되어도 주문 기록은 유지
    @ManyToOne(optional = true)
    @JoinColumn(name = "warehouse_id", nullable = true, foreignKey = @ForeignKey(name = "fk_order_warehouse", foreignKeyDefinition = "FOREIGN KEY (warehouse_id) REFERENCES warehouse (id) ON DELETE SET NULL"))
    private Warehouse warehouse;
    
    // 삭제된 창고 정보를 보존하기 위한 필드들
    @Column(name = "warehouse_code")
    private String warehouseCode;
    
    @Column(name = "warehouse_name")
    private String warehouseName;

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
        
        // 제품과 창고 정보 백업
        if (product != null) {
            this.productCode = product.getCode();
            this.productName = product.getName();
        }
        
        if (warehouse != null) {
            this.warehouseCode = warehouse.getCode();
            this.warehouseName = warehouse.getName();
        }
    }
}
