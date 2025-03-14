package com.example.kafkaorder.service;

import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.model.QOrder;
import com.example.kafkaorder.repository.OrderRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final JPAQueryFactory queryFactory;

    public OrderService(OrderRepository orderRepository, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // 전체 주문 목록 조회
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 주문 저장
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // 주문 ID로 주문 조회
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

//    //QueryDSL을 이용한 동적 검색 예제: 특정 상태의 주문 조회
    public List<Order> getOrdersByStatus(String status) {
        QOrder order = QOrder.order;
        BooleanExpression condition = order.status.eq(status);
        return queryFactory.selectFrom(order)
                .where(condition)
                .fetch();
    }
}
