package com.example.kafkaorder.controller;

import com.example.kafkaorder.kafka.OrderProducer;
import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.service.OrderCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderCacheService orderCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderController(OrderProducer orderProducer, OrderCacheService orderCacheService) {
        this.orderProducer = orderProducer;
        this.orderCacheService = orderCacheService;
    }

    // 주문 생성: POST 요청 시 Order 객체를 받아 JSON으로 Kafka에 전송
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            orderProducer.sendOrder(orderJson);
            return ResponseEntity.ok("주문 메시지가 성공적으로 전송되었습니다.");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 전송 실패");
        }
    }

    // 주문 조회: Redis 캐시에서 주문 정보를 가져옴
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderCacheService.getOrder(orderId);
        if(order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
