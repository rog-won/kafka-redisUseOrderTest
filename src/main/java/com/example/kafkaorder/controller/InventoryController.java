package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.InventoryDto;
import com.example.kafkaorder.entity.Inventory;
import com.example.kafkaorder.entity.InventoryHistory;
import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public InventoryController(InventoryService inventoryService,
                               ProductService productService,
                               WarehouseService warehouseService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    // 재고 목록 조회 페이지
    @GetMapping
    public String listInventory(Model model) {
        model.addAttribute("inventories", inventoryService.getAllInventories());
        return "/view/inventory/inventoryList"; // templates/inventoryList.html
    }
    
    // 재고 히스토리 목록 조회 페이지
    @GetMapping("/history")
    public String listInventoryHistory(Model model) {
        List<InventoryHistory> historyList = inventoryService.getAllInventoryHistory();
        model.addAttribute("historyList", historyList);
        return "/view/inventory/inventoryHistory"; // templates/inventoryHistory.html
    }

    // 재고 입고 등록 폼 호출
    @GetMapping("/new")
    public String newInventoryForm(Model model) {
        // 기본 DTO 생성
        InventoryDto inventoryDto = new InventoryDto();
        
        model.addAttribute("inventoryDto", inventoryDto);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "/view/inventory/inventoryForm"; // templates/inventoryForm.html
    }

    // 재고 입고 등록 처리
    @PostMapping
    public String createInventory(@ModelAttribute("inventoryDto") InventoryDto inventoryDto) {
        log.info("inventoryDto: {}", inventoryDto);
        
        // 사용자가 폼에서 제공한 등록자 정보를 그대로 사용
        inventoryService.addOrUpdateInventoryFromDto(inventoryDto);
        return "redirect:/inventory";
    }

    @GetMapping("/{id}")
    public String viewInventory(@PathVariable Long id, Model model) {
        try {
            Inventory inventory = inventoryService.getInventoryById(id);
            model.addAttribute("inventory", inventory);
            return "view/inventory/inventoryDetail";
        } catch (Exception e) {
            model.addAttribute("error", "해당 재고를 찾을 수 없습니다: " + e.getMessage());
            return "redirect:/inventory";
        }
    }
}