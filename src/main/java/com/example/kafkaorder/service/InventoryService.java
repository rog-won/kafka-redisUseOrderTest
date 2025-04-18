package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.InventoryDto;
import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.InventoryHistory;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.repository.InventoryHistoryRepository;
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
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryHistoryRepository inventoryHistoryRepository,
                            ProductRepository productRepository,
                            WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryHistoryRepository = inventoryHistoryRepository;
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
        
        // 재고 내역 저장 (입고)
        createInventoryHistory(product, warehouse, inventory.getQuantity(), dto.getRegisteredBy(), "입고", dto.getNotes());
        
        // 기존 재고가 있는지 조회 후 업데이트 혹은 신규 생성
        List<Inventory> existingInventories = inventoryRepository.findByProductAndWarehouse(product, warehouse);
        if(!existingInventories.isEmpty()){
            Inventory existing = existingInventories.get(0);
            existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
            // 등록자 정보 유지 (기존 등록자가 없는 경우에만 새로운 등록자로 업데이트)
            if (existing.getRegisteredBy() == null || existing.getRegisteredBy().isEmpty()) {
                existing.setRegisteredBy(inventory.getRegisteredBy());
            }
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
        
        // 재고 내역 저장 (출고)
        createInventoryHistory(inventory.getProduct(), inventory.getWarehouse(), -quantity, "시스템", "출고", "주문 처리");
        
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void processOrderAcceptance(Order order, String warehouseCode) {
        // 제품이나 창고가 삭제되었는지 확인
        if (order.getProduct() == null) {
            throw new RuntimeException(String.format(
                "제품이 삭제되어 주문을 처리할 수 없습니다: 제품 코드 '%s'", 
                order.getProductCode()
            ));
        }
        
        if (order.getWarehouse() == null) {
            throw new RuntimeException(String.format(
                "창고가 삭제되어 주문을 처리할 수 없습니다: 창고 코드 '%s'", 
                order.getWarehouseCode()
            ));
        }
        
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
    
    // 특정 제품 코드에 해당하는 모든 재고 삭제
    @Transactional
    public void deleteInventoryByProductCode(String productCode) {
        Optional<Product> productOptional = productRepository.findByCode(productCode);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // 해당 제품과 관련된 모든 재고 찾기
            List<Inventory> inventories = inventoryRepository.findAll().stream()
                .filter(inventory -> inventory.getProduct().getId().equals(product.getId()))
                .toList();
            
            // 재고 내역 저장 (삭제)
            for (Inventory inventory : inventories) {
                createInventoryHistory(inventory.getProduct(), inventory.getWarehouse(), 
                    -inventory.getQuantity(), "시스템", "삭제", "제품 삭제로 인한 재고 조정");
            }
            
            // 모든 재고 삭제
            inventoryRepository.deleteAll(inventories);
        }
    }
    
    // 특정 창고 코드에 해당하는 모든 재고 삭제
    @Transactional
    public void deleteInventoryByWarehouseCode(String warehouseCode) {
        Optional<Warehouse> warehouseOptional = warehouseRepository.findByCode(warehouseCode);
        if (warehouseOptional.isPresent()) {
            Warehouse warehouse = warehouseOptional.get();
            // 해당 창고와 관련된 모든 재고 찾기
            List<Inventory> inventories = inventoryRepository.findAll().stream()
                .filter(inventory -> inventory.getWarehouse().getId().equals(warehouse.getId()))
                .toList();
            
            // 재고 내역 저장 (삭제)
            for (Inventory inventory : inventories) {
                createInventoryHistory(inventory.getProduct(), inventory.getWarehouse(), 
                    -inventory.getQuantity(), "시스템", "삭제", "창고 삭제로 인한 재고 조정");
            }
            
            // 모든 재고 삭제
            inventoryRepository.deleteAll(inventories);
        }
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다. ID: " + id));
    }
    
    // 재고 내역 생성 메서드
    private InventoryHistory createInventoryHistory(Product product, Warehouse warehouse, 
                                                   int quantityChange, String registeredBy, 
                                                   String actionType, String notes) {
        InventoryHistory history = InventoryHistory.builder()
                .product(product)
                .warehouse(warehouse)
                .quantityChange(quantityChange)
                .registeredBy(registeredBy)
                .actionType(actionType)
                .notes(notes)
                .build();
        
        return inventoryHistoryRepository.save(history);
    }
    
    // 재고 내역 조회
    public List<InventoryHistory> getAllInventoryHistory() {
        return inventoryHistoryRepository.findAll();
    }
}