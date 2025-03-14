package com.example.kafkaorder.controller;

import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderMvcController {


    private final OrderService orderService;
    private final InventoryService inventoryService;

    public OrderMvcController(OrderService orderService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    // 주문 목록 조회 (Thymeleaf 템플릿)
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders"; // 예: src/main/resources/templates/orders.html
    }

    // 신규 주문 생성 폼 호출
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "orderForm"; // 예: src/main/resources/templates/orderForm.html
    }

    // 폼 제출 시 주문 저장 (DB에 직접 저장)
    @PostMapping
    public String createOrder(@ModelAttribute Order order) {
        order.setStatus("CREATED");
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    // 주문 수락: 주문 상태를 ACCEPTED로 변경하고, 지정한 창고(예: "1")에서 재고 출고 처리
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam String orderId, @RequestParam(defaultValue = "1") String warehouseId) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus("ACCEPTED");
            orderService.saveOrder(order);
            // 재고 차감 처리 (출고)
            inventoryService.processOrderAcceptance(order, warehouseId);
            return "redirect:/orders";
        } else {
            return "redirect:/orders?error=OrderNotFound";
        }
    }
}
