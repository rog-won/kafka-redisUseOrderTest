package com.example.kafkaorder.controller;

import com.example.kafkaorder.kafka.OrderProducer;
import com.example.kafkaorder.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

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
}
