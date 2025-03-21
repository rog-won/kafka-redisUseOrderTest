package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.service.OrderService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public OrderController(OrderService orderService, ProductService productService, WarehouseService warehouseService) {
        this.orderService = orderService;
        this.productService = productService;
        this.warehouseService = warehouseService;
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

    // 주문 생성 처리
    @PostMapping
    public String createOrder(@ModelAttribute("orderDto") OrderDto orderDto) {
        orderService.createOrderFromDto(orderDto);
        return "redirect:/orders";
    }
}
