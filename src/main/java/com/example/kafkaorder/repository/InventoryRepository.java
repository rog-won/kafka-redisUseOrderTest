package com.example.kafkaorder.repository;

import com.example.kafkaorder.model.Inventory;
import com.example.kafkaorder.model.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
}
