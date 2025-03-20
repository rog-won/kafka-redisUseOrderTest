package com.example.kafkaorder.controller;

import com.example.kafkaorder.model.InventoryInboundDto;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public InventoryController(InventoryService inventoryService, ProductService productService, WarehouseService warehouseService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    // 재고 목록 조회 페이지
    @GetMapping
    public String listInventory(Model model) {
        model.addAttribute("inventories", inventoryService.getAllInventories());
        return "/view/inventory/inventory"; // 예: src/main/resources/templates/inventory.html
    }

    // 입고 등록 폼 호출
    @GetMapping("/inbound")
    public String inboundForm(Model model) {
        model.addAttribute("inventoryInbound", new InventoryInboundDto());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "/view/inventory/inventoryInboundForm"; // 예: src/main/resources/templates/inventoryInboundForm.html
    }

    // 입고 등록 처리
    @PostMapping("/inbound")
    public String processInbound(@ModelAttribute InventoryInboundDto inboundDto) {
        inventoryService.processInbound(inboundDto.getProductId(), inboundDto.getQuantity(), inboundDto.getCode());
        return "redirect:/inventory";
    }
}