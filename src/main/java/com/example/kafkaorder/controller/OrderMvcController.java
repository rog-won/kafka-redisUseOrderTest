package com.example.kafkaorder.controller;

import com.example.kafkaorder.model.Inventory;
import com.example.kafkaorder.model.InventoryId;
import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.OrderService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderMvcController {

    private final OrderService orderService;
    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final InventoryService inventoryService;

    public OrderMvcController(OrderService orderService, ProductService productService, InventoryService inventoryService, WarehouseService warehouseService) {
        this.orderService = orderService;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
    }

    // 주문 목록 조회 (Thymeleaf 템플릿)
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "/view/order/orders"; // 예: src/main/resources/templates/orders.html
    }

    // 신규 주문 생성 폼 호출
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        Order order = new Order();
        model.addAttribute("order", order);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "/view/order/orderForm"; // 예: src/main/resources/templates/orderForm.html
    }

    // 주문 생성 처리 (재고 부족 체크)
    @PostMapping
    public String createOrder(@ModelAttribute Order order, Model model) {
        // 주문 생성 전에 재고를 확인 (선택된 창고와 제품에 대해)
        InventoryId invId = new InventoryId(order.getProduct().getProductId(), order.getWarehouseCode());
        Inventory inventory = inventoryService.getInventoryById(invId);
        if(inventory.getQuantity() < order.getQuantity()) {
            model.addAttribute("error", "재고 부족: 제품 " + order.getProduct().getProductId());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("warehouses", warehouseService.getAllWarehouses());
            return "/view/order/orderForm";
        }
        order.setStatus("CREATED");
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    // 주문 수락: 주문 상태를 ACCEPTED로 변경하고, 지정한 창고(기본 "1")에서 재고 출고 처리
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam String orderId, String warehouseCode) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus("ACCEPTED");
            orderService.saveOrder(order);
            inventoryService.processOrderAcceptance(order, warehouseCode);
        }
        return "redirect:/orders";
    }

    // 주문 취소: 주문 상태를 CANCELED로 변경하고 (재고 복구 로직은 추가 가능)
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam String orderId) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus("CANCELED");
            orderService.saveOrder(order);
            // 재고 복구 로직이 필요하다면 추가 구현 (예: inventoryService.processOrderCancellation(order, warehouseId))
        }
        return "redirect:/orders";
    }
}