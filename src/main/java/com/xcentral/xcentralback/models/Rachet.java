package com.xcentral.xcentralback.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Rachet {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rachet;

    // Getter for id
    public Long getId() {
        return id;
    }

 
    // Getter for rachetName
    public String getRachet() {
        return rachet;
    }

}