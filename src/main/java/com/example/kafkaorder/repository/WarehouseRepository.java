package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
}
