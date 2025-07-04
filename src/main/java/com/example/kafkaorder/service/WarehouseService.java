package com.example.kafkaorder.service;

import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.exception.ResourceNotFoundException;
import com.example.kafkaorder.exception.ErrorCode;
import com.example.kafkaorder.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    public WarehouseService(WarehouseRepository warehouseRepository, 
                           InventoryService inventoryService) {
        this.warehouseRepository = warehouseRepository;
        this.inventoryService = inventoryService;
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Warehouse saveWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }
    
    // 창고 삭제 기능
    @Transactional
    public void deleteWarehouse(String code) {
        Optional<Warehouse> warehouseOptional = warehouseRepository.findByCode(code);
        if (warehouseOptional.isPresent()) {
            Warehouse warehouse = warehouseOptional.get();
            
            // 1. 관련 재고 삭제
            inventoryService.deleteInventoryByWarehouseCode(code);
            
            // 2. 창고 자체 삭제
            warehouseRepository.delete(warehouse);
        } else {
            throw new ResourceNotFoundException(ErrorCode.WAREHOUSE_NOT_FOUND, "존재하지 않는 창고 코드입니다: " + code);
        }
    }
    
    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }
}
