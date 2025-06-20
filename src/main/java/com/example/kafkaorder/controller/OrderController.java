package com.example.kafkaorder.controller;

import com.example.kafkaorder.dto.OrderDto;
import com.example.kafkaorder.entity.Order;
import com.example.kafkaorder.exception.ResourceNotFoundException;
import com.example.kafkaorder.exception.BusinessException;
import com.example.kafkaorder.exception.ErrorCode;
import com.example.kafkaorder.service.InventoryService;
import com.example.kafkaorder.service.OrderService;
import com.example.kafkaorder.service.ProductService;
import com.example.kafkaorder.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

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

    // 주문 상세 조회
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        try {
            Order order = orderService.getOrderById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다: " + id));
            model.addAttribute("order", order);
            return "/view/order/orderDetail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orders", orderService.getAllOrders());
            return "/view/order/orderList";
        }
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
    @PostMapping("/new")
    public String createOrder(@Valid @ModelAttribute OrderDto orderDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // 검증 실패 시 폼으로 다시 돌아가기
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("warehouses", warehouseService.getAllWarehouses());
            return "/view/order/orderForm";
        }
        
        try {
            Order order = orderService.createOrderFromDto(orderDto);
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("warehouses", warehouseService.getAllWarehouses());
            return "/view/order/orderForm";
        }
    }

    //TODO
    // 주문 수락 처리: 주문 상태를 ACCEPTED로 변경하고 재고에서 주문 수량을 차감
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam Long orderId, @RequestParam(required = false) String username, Model model) {
        try {
            // 트랜잭션이 적용된 서비스 메서드 사용
            orderService.processOrderAcceptance(orderId, username);
            return "redirect:/orders";
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 표시
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orders", orderService.getAllOrders());
            return "/view/order/orderList";
        }
    }

    //TODO
    // 주문 취소 처리: 주문 상태를 CANCELED로 변경 (재고 가감은 하지 않음)
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId, @RequestParam(required = false) String username, Model model) {
        try {
            // 트랜잭션이 적용된 서비스 메서드 사용
            orderService.processOrderCancellation(orderId, username);
            return "redirect:/orders";
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 표시
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orders", orderService.getAllOrders());
            return "/view/order/orderList";
        }
    }
}
