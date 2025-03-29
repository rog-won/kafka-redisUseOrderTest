package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.OrderService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Period;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;
    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final InventoryService inventoryService;

    public OrderController(OrderService orderService, ProductService productService, WarehouseService warehouseService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.inventoryService = inventoryService;
    }

    // 주문 목록 조회
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "/view/order/orderList"; // templates/orderList.html
    }

    // 신규 주문 생성 폼 호출
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        Order order = new Order();
        model.addAttribute("orderDto", new OrderDto());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "/view/order/orderForm"; // templates/orderForm.html
    }

    //TODO
    // 주문 수락 처리: 주문 상태를 ACCEPTED로 변경하고 재고에서 주문 수량을 차감
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam Long orderId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));
        // 재고 차감 처리 (재고 부족이면 예외 발생)
        inventoryService.processOrderAcceptance(order, order.getWarehouseCode());
        order.setStatus("ACCEPTED");
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    //TODO
    // 주문 취소 처리: 주문 상태를 CANCELED로 변경 (재고 가감은 하지 않음)
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));
        order.setStatus("CANCELED");
        orderService.saveOrder(order);
        return "redirect:/orders";
    }
}
