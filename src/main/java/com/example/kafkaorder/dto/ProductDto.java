package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Product;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductDto {
    @Size(max = 50, message = "제품 코드는 50자 이하여야 합니다")
    private String code; // 자동 생성되므로 필수 아님
    
    @NotBlank(message = "제품명은 필수입니다")
    @Size(max = 100, message = "제품명은 100자 이하여야 합니다")
    private String name;
    
    @Size(max = 500, message = "제품 설명은 500자 이하여야 합니다")
    private String description;
    
    @NotNull(message = "가격은 필수입니다")
    @Min(value = 1, message = "가격은 1 이상이어야 합니다")
    private Integer price;
    
    // 이미지 경로는 파일 업로드 시 자동 설정되므로 검증하지 않음
    private String imagePath;

    /**
     * DTO를 Product 엔티티로 변환
     */
    public Product toEntity() {
        Product product = new Product();
        product.setCode(this.code);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setImagePath(this.imagePath);
        return product;
    }

    /**
     * Product 엔티티를 DTO로 변환 (수정 폼용)
     */
    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImagePath(product.getImagePath());
        return dto;
    }
} 