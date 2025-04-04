package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrderFromDto(OrderDto dto);
    
    List<Order> getAllOrders();
    
    Optional<Order> getOrderById(Long id);
    
    void saveOrder(Order order);
}
