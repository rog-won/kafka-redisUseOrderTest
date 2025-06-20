package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.exception.ResourceNotFoundException;
import com.example.kafkaorder.exception.BusinessException;
import com.example.kafkaorder.exception.ErrorCode;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        WarehouseRepository warehouseRepository,
                        InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order createOrderFromDto(OrderDto dto) {
        // 제품 코드를 사용해 실제 Product 조회
        Product product = productRepository.findByCode(dto.getProductCode())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "제품을 찾을 수 없습니다: " + dto.getProductCode()));
        Warehouse warehouse = warehouseRepository.findByCode(dto.getWarehouseCode())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WAREHOUSE_NOT_FOUND, "창고를 찾을 수 없습니다: " + dto.getWarehouseCode()));
        
        // DTO를 엔티티로 변환
        Order order = dto.toEntity(product, warehouse);
        
        // 항상 새로운 주문 생성 (중복 체크 없음)
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    /**
     * 주문 수락 처리 - 주문 상태 변경과 재고 차감을 하나의 트랜잭션으로 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public void processOrderAcceptance(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다: " + orderId));
        
        // 이미 처리된 주문인지 확인
        if ("ACCEPTED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PROCESSED, "이미 수락된 주문입니다: " + orderId);
        }
        
        if ("CANCELED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELED, "취소된 주문은 수락할 수 없습니다: " + orderId);
        }
        
        // 재고 확인 및 차감 처리 (동시성 제어 적용)
        inventoryService.processOrderAcceptance(order, order.getWarehouse().getCode());
        
        // 주문 상태 변경 및 처리 정보 업데이트
        order.setStatus("ACCEPTED");
        order.setStatusChangedAt(java.time.LocalDateTime.now());
        order.setStatusChangedBy(username != null ? username : "관리자");
        
        orderRepository.save(order);
    }

    /**
     * 주문 취소 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public void processOrderCancellation(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다: " + orderId));
        
        // 이미 처리된 주문인지 확인
        if ("CANCELED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PROCESSED, "이미 취소된 주문입니다: " + orderId);
        }
        
        if ("ACCEPTED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELED, "이미 수락된 주문은 취소할 수 없습니다: " + orderId);
        }
        
        // 주문 상태 변경 및 처리 정보 업데이트
        order.setStatus("CANCELED");
        order.setStatusChangedAt(java.time.LocalDateTime.now());
        order.setStatusChangedBy(username != null ? username : "관리자");
        
        orderRepository.save(order);
    }
} 