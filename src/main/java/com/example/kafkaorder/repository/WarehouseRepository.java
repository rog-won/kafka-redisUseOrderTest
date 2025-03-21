package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    Optional<Warehouse> findByCode(String code);

}
