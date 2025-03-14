package com.example.kafkaorder.kafka;

import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.service.OrderCacheService;
import com.example.kafkaorder.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    private final OrderService orderService;
//    private final OrderCacheService orderCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderConsumer(OrderService orderService/*, OrderCacheService orderCacheService*/) {
        this.orderService = orderService;
//        this.orderCacheService = orderCacheService;
    }

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrder(String orderData) {
        try {
            // Kafka로부터 전달받은 JSON 메시지를 Order 객체로 변환
            Order order = objectMapper.readValue(orderData, Order.class);

            // 주문을 DB에 저장 (영속화)
            Order savedOrder = orderService.saveOrder(order);

            // 저장된 주문을 Redis에 캐싱 (빠른 조회 및 트래픽 분산) 주문 완료시 캐싱 필요없어서 주석처리.
//            orderCacheService.cacheOrder(savedOrder);

            System.out.println("주문 처리 완료: " + savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
