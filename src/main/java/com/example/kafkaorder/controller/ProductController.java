package com.example.kafkaorder.controller;

import com.example.kafkaorder.model.Product;
import com.example.kafkaorder.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 생성자 주입
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 제품 목록 조회
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // templates/products.html
    }

    // 신규 제품 등록 폼 호출
    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "productForm"; // templates/productForm.html
    }

    // 제품 등록 처리
    @PostMapping
    public String createProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }
}