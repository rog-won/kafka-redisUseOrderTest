package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.repository.OrderRepository;
import com.example.kafkaorder.repository.ProductRepository;
import com.example.kafkaorder.repository.WarehouseRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public OrderServiceImpl(OrderRepository orderRepository, 
                         ProductRepository productRepository, 
                         WarehouseRepository warehouseRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    @Transactional
    public Order createOrderFromDto(OrderDto dto) {
        // 제품 코드를 사용해 실제 Product 조회
        Product product = productRepository.findByCode(dto.getProductCode())
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다: " + dto.getProductCode()));
        Warehouse warehouse = warehouseRepository.findByCode(dto.getWarehouseCode())
                .orElseThrow(() -> new RuntimeException("창고를 찾을 수 없습니다: " + dto.getWarehouseCode()));
        
        // DTO를 엔티티로 변환
        Order order = dto.toEntity(product, warehouse);
        
        // 항상 새로운 주문 생성 (중복 체크 없음)
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
} 