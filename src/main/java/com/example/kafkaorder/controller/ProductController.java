package com.example.kafkaorder.controller;

import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.service.FileStorageService;
import com.example.kafkaorder.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    // 생성자 주입
    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
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

    // 제품 등록 처리 - 이미지 업로드 기능 추가
    @PostMapping
    public String createProduct(@ModelAttribute Product product, 
                               @RequestParam(value = "image", required = false) MultipartFile image) {
        // 이미지 파일이 제공되었으면 저장
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(image);
                product.setImagePath(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // 에러 처리 로직 추가 가능
            }
        }
        
        productService.saveProduct(product);
        return "redirect:/product/list";
    }
    
    // 제품 삭제 처리 - ID 대신 코드 사용
    @PostMapping("/delete/{code}")
    public String deleteProduct(@PathVariable String code) {
        productService.deleteProduct(code);
        return "redirect:/product/list";
    }
    
    // 이미지 파일 조회 API
    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if(resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}