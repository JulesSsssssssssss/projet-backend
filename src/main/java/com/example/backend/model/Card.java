package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String rarity; // COMMON, RARE, EPIC, LEGENDARY
    
    @Column(nullable = false)
    private Integer attack;
    
    @Column(nullable = false)
    private Integer defense;
    
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
