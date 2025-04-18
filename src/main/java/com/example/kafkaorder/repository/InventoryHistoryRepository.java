package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.InventoryHistory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    // 제품별 재고 히스토리 조회
    List<InventoryHistory> findByProductOrderByCreatedAtDesc(Product product);
    
    // 창고별 재고 히스토리 조회
    List<InventoryHistory> findByWarehouseOrderByCreatedAtDesc(Warehouse warehouse);
    
    // 제품 및 창고별 재고 히스토리 조회
    List<InventoryHistory> findByProductAndWarehouseOrderByCreatedAtDesc(Product product, Warehouse warehouse);
    
    // 액션 타입별 조회
    List<InventoryHistory> findByActionTypeOrderByCreatedAtDesc(String actionType);
} 