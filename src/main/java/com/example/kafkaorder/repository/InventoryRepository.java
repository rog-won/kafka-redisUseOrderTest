package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);

}
