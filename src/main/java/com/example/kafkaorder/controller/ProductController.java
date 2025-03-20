package com.example.kafkaorder.controller;

import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    // 생성자 주입
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 제품 목록 조회
    @GetMapping("/list")
    public String listProducts(Model model) {
        model.addAttribute("productList", productService.getAllProducts());
        return "view/product/productList"; // templates/productList.html
    }

    // 신규 제품 등록 폼 호출
    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "/view/product/productForm"; // templates/productForm.html
    }

    // 제품 등록 처리
    @PostMapping
    public String createProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/product/list";
    }
}