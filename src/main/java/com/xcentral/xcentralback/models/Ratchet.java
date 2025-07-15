package com.xcentral.xcentralback.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratchet")
public class Ratchet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    private String ratchet;

    // Getter for id
    public Long getId() {
        return id;
    }

    // Getter for ratchet
    @JsonProperty("name")
    public String getRatchet() {
        return ratchet;
    }

    // Setter for ratchet
    @JsonProperty("name")
    public void setRatchet(String ratchet) {
        this.ratchet = ratchet;
    }

}