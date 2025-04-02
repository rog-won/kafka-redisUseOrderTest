package com.example.kafkaorder.service;

import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final InventoryService inventoryService;

    public ProductService(ProductRepository productRepository, FileStorageService fileStorageService, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
        this.inventoryService = inventoryService;
    }

    // 제품 저장
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // 전체 제품 목록 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findByCode(String code) {
        return productRepository.findByCode(code);
    }

    // 특정 제품 조회 (필요 시)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
    
    // 제품 삭제 (이미지 파일도 함께 삭제)
    @Transactional
    public void deleteProduct(String code) {
        Optional<Product> productOptional = productRepository.findByCode(code);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            
            // 제품에 이미지가 있으면 이미지 파일도 삭제
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                fileStorageService.deleteFile(product.getImagePath());
            }
            
            // 재고 삭제
            inventoryService.deleteInventoryByProductCode(code);
            
            // 제품 삭제
            productRepository.deleteById(product.getId());
        } else {
            throw new RuntimeException("존재하지 않는 제품 코드입니다: " + code);
        }
    }
}