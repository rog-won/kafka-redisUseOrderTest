package com.example.kafkaorder.service;

import com.example.kafkaorder.entity.Product;
import com.example.kafkaorder.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 제품 저장
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // 전체 제품 목록 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 특정 제품 조회 (필요 시)
    public Product getProductById(String productId) {
        return productRepository.findById(productId).orElse(null);
    }
}