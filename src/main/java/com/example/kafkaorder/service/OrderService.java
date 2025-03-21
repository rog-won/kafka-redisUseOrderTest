package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.repository.OrderRepository;
import com.example.kafkaorder.repository.ProductRepository;
import com.example.kafkaorder.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository; // Product 조회를 위해
    private final WarehouseRepository warehouseRepository; // Product 조회를 위해

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public Order createOrderFromDto(OrderDto dto) {
        // 제품 코드를 사용해 실제 Product 조회
        Product product = productRepository.findByCode(dto.getProductCode())
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다: " + dto.getProductCode()));
        Warehouse warehouse = warehouseRepository.findByCode(dto.getWarehouseCode())
                .orElseThrow(() -> new RuntimeException("창고를 찾을 수 없습니다: " + dto.getWarehouseCode()));
        // DTO를 엔티티로 변환
        Order order = dto.toEntity(product, warehouse);

        Optional<Order> existingOpt = orderRepository.findByProductAndWarehouse(product, warehouse);
        if(existingOpt.isPresent()){
            Order existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity() + order.getQuantity());
            return orderRepository.save(existing);
        } else {
            return orderRepository.save(order);
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
