package com.example.kafkaorder.service;

import com.example.kafkaorder.model.Inventory;
import com.example.kafkaorder.model.InventoryId;
import com.example.kafkaorder.model.InventoryTransaction;
import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.repository.InventoryRepository;
import com.example.kafkaorder.repository.InventoryTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryTransactionRepository transactionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * 주문 수락 시 재고에서 주문 수량만큼 차감하고 트랜잭션 기록
     */
    @Transactional
    public void processOrderAcceptance(Order order, String Code) {
        // 특정 창고의 재고 조회 (없으면 0부터 시작)
        InventoryId invId = new InventoryId(order.getProduct().getProductId(), Code);
        Inventory inventory = inventoryRepository.findById(invId).orElse(new Inventory(invId, 0));

        // 디버깅 로그 추가
        System.out.println("재고 확인 - 제품: " + order.getProduct().getProductId() +
                ", 창고: " + Code +
                ", 현재 재고: " + inventory.getQuantity() +
                ", 주문 수량: " + order.getQuantity());

        // 주문 수량만큼 재고 차감
        int newQuantity = inventory.getQuantity() - order.getQuantity();
        if(newQuantity < 0) {
            throw new RuntimeException("재고 부족: 제품 " + order.getProduct().getProductId());
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        // 재고 출고 트랜잭션 기록
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(order.getProduct().getProductId());
        transaction.setCode(Code);
        transaction.setQuantity(order.getQuantity());
        transaction.setType("OUTBOUND");
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setOrderId(order.getOrderId());
        transactionRepository.save(transaction);
    }

    // 신규 입고 처리 (재고 증가)
    @Transactional
    public void processInbound(String productId, int quantity, String Code) {
        InventoryId invId = new InventoryId(productId, Code);
        Inventory inventory = inventoryRepository.findById(invId).orElse(new Inventory(invId, 0));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(productId);
        transaction.setCode(Code);
        transaction.setQuantity(quantity);
        transaction.setType("INBOUND");
        transaction.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    // 전체 재고 목록 조회 (재고 상태 확인용)
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // 재고 조회 (주문 생성 시 사용)
    public Inventory getInventoryById(InventoryId invId) {
        return inventoryRepository.findById(invId).orElse(new Inventory(invId, 0));
    }
}