package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.WarehouseDto;
import com.example.kafkaorder.entity.Warehouse;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
        model.addAttribute("warehouseDto", new WarehouseDto());
        return "/view/warehouse/warehouseForm"; // templates/warehouseForm.html
    }

    // 창고 등록 처리
    @PostMapping
    public String createWarehouse(@Valid @ModelAttribute WarehouseDto warehouseDto, 
                                 BindingResult bindingResult, 
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // 검증 실패 시 폼으로 다시 돌아가기
            return "/view/warehouse/warehouseForm";
        }
        
        try {
            Warehouse warehouse = warehouseDto.toEntity();
            warehouseService.saveWarehouse(warehouse);
            return "redirect:/warehouses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/view/warehouse/warehouseForm";
        }
    }
    
    // 창고 삭제 처리 - ID 대신 코드 사용
    @PostMapping("/delete/{code}")
    public String deleteWarehouse(@PathVariable String code) {
        warehouseService.deleteWarehouse(code);
        return "redirect:/warehouses";
    }
}