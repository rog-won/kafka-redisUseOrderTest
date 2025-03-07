package com.example.kafkaorder.kafka;

import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.service.OrderCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    private final OrderCacheService orderCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderConsumer(OrderCacheService orderCacheService) {
        this.orderCacheService = orderCacheService;
    }

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrder(String orderData) {
        try {
            Order order = objectMapper.readValue(orderData, Order.class);
            orderCacheService.cacheOrder(order);
            System.out.println("수신 및 캐시된 주문: " + order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
