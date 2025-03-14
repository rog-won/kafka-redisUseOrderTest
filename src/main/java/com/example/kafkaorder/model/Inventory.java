package com.example.kafkaorder.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @EmbeddedId
    private InventoryId id;

    @Column(nullable = false)
    private int quantity;
}

