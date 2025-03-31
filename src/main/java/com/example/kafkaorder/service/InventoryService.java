package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.InventoryDto;
import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.repository.InventoryRepository;
import com.example.kafkaorder.repository.ProductRepository;
import com.example.kafkaorder.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public Inventory addOrUpdateInventoryFromDto(InventoryDto dto) {
        // 서비스 계층에서 제품과 창고 조회 및 검증
        Product product = productRepository.findByCode(dto.getProductCode())
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다: " + dto.getProductCode()));
        Warehouse warehouse = warehouseRepository.findByCode(dto.getWarehouseCode())
                .orElseThrow(() -> new RuntimeException("창고를 찾을 수 없습니다: " + dto.getWarehouseCode()));

        // DTO를 엔티티로 변환
        Inventory inventory = dto.toEntity(product, warehouse);
        // 기존 재고가 있는지 조회 후 업데이트 혹은 신규 생성
        List<Inventory> existingInventories = inventoryRepository.findByProductAndWarehouse(product, warehouse);
        if(!existingInventories.isEmpty()){
            Inventory existing = existingInventories.get(0);
            existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
            return inventoryRepository.save(existing);
        } else {
            return inventoryRepository.save(inventory);
        }
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    // 재고 차감 (주문 수락 시)
    @Transactional
    public void deductInventory(Inventory inventory, int quantity) {
        int newQuantity = inventory.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new RuntimeException(String.format(
                "재고 부족: 제품 '%s', 현재 재고: %d, 필요 수량: %d", 
                inventory.getProduct().getCode(), 
                inventory.getQuantity(), 
                quantity
            ));
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void processOrderAcceptance(Order order, String warehouseCode) {
        // 창고와 제품으로 재고 조회
        List<Inventory> inventories = inventoryRepository.findByProductAndWarehouse(order.getProduct(), order.getWarehouse());
        
        if (inventories.isEmpty()) {
            throw new RuntimeException(String.format(
                "재고 정보를 찾을 수 없습니다: 제품 '%s', 창고 '%s'", 
                order.getProduct().getCode(), 
                warehouseCode
            ));
        }
        
        // 첫 번째 재고 항목 사용
        Inventory inventory = inventories.get(0);
        
        // 재고 차감
        deductInventory(inventory, order.getQuantity());
    }
}