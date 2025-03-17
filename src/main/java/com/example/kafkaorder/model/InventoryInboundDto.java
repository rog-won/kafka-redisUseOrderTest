package com.example.kafkaorder.model;

import lombok.Data;

@Data
public class InventoryInboundDto {

    private String productId;
    private int quantity;
    private String code;

}
