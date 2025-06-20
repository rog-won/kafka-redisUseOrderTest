package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
    
    /**
     * 비관적 락을 사용하여 재고 정보를 조회합니다.
     * 동시에 여러 주문이 들어와도 재고 차감에서 데이터 무결성을 보장합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product = :product AND i.warehouse = :warehouse")
    List<Inventory> findByProductAndWarehouseWithLock(@Param("product") Product product, @Param("warehouse") Warehouse warehouse);
    
    /**
     * 단일 재고 항목을 락과 함께 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdWithLock(@Param("id") Long id);

}
