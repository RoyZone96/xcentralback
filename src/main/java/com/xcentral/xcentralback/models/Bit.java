package com.xcentral.xcentralback.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bittype")
public class Bit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bit;

    // Getter for id
    public Long getId() {
        return id;
    }


    // Getter for bitName
    public String getBit() {
        return bit;
    }

    // Setter for bitName
    public void setBit(String bit) {
        this.bit = bit;
    }
}
