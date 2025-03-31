package com.example.kafkaorder.repository;

import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByProductAndWarehouse(Product product, Warehouse warehouse);

}
