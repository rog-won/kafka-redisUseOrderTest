package com.example.kafkaorder.dto;

import com.example.kafkaorder.entity.Warehouse;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class WarehouseDto {
    @NotBlank(message = "창고 코드는 필수입니다")
    @Size(max = 50, message = "창고 코드는 50자 이하여야 합니다")
    private String code;
    
    @NotBlank(message = "창고명은 필수입니다")
    @Size(max = 100, message = "창고명은 100자 이하여야 합니다")
    private String name;
    
    @Size(max = 200, message = "위치는 200자 이하여야 합니다")
    private String location;

    /**
     * DTO를 Warehouse 엔티티로 변환
     */
    public Warehouse toEntity() {
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(this.code);
        warehouse.setName(this.name);
        warehouse.setLocation(this.location);
        return warehouse;
    }

    /**
     * Warehouse 엔티티를 DTO로 변환 (수정 폼용)
     */
    public static WarehouseDto fromEntity(Warehouse warehouse) {
        WarehouseDto dto = new WarehouseDto();
        dto.setCode(warehouse.getCode());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        return dto;
    }
} 