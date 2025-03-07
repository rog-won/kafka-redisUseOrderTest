package com.example.kafkaorder.service;

import com.example.kafkaorder.model.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderCacheService {

    private final RedisTemplate<String, Order> redisTemplate;

    public OrderCacheService(RedisTemplate<String, Order> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheOrder(Order order) {
        redisTemplate.opsForValue().set(order.getOrderId(), order);
    }

    public Order getOrder(String orderId) {
        return redisTemplate.opsForValue().get(orderId);
    }

}
