package com.xcentral.xcentralback.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "blade_parts")
public class Blade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bladeName")
    private String bladeName;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return bladeName;
    }

  
}