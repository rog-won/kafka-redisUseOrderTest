package com.example.kafkaorder.controller;

import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // 창고 목록 조회
    @GetMapping
    public String listWarehouses(Model model) {
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "/view/warehouse/warehouses";  // templates/warehouses.html
    }

    // 신규 창고 등록 폼 호출
    @GetMapping("/new")
    public String newWarehouseForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "/view/warehouse/warehouseForm"; // templates/warehouseForm.html
    }

    // 창고 등록 처리
    @PostMapping
    public String createWarehouse(@ModelAttribute Warehouse warehouse) {
        warehouseService.saveWarehouse(warehouse);
        return "redirect:/warehouses";
    }
    
    // 창고 삭제 처리 - ID 대신 코드 사용
    @PostMapping("/delete/{code}")
    public String deleteWarehouse(@PathVariable String code) {
        warehouseService.deleteWarehouse(code);
        return "redirect:/warehouses";
    }
}