package com.xcentral.xcentralback.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.TemporalType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.FetchType;
import jakarta.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String blade;
    private String ratchet;
    private String bit;
    private int wins;
    private int losses;
    private Double winRateAvg;
    private int points;
    private Double pointsAvg;
    private boolean isFlagged;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    @Column(name = "username")
    private String username;

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = new Date();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlade() {
        return blade;
    }

    public void setBlade(String blade) {
        this.blade = blade;
    }

    public String getRatchet() {
        return ratchet;
    }

    public void setRatchet(String ratchet) {
        this.ratchet = ratchet;
    }

    public String getBit() {
        return bit;
    }

    public void setBit(String bit) {
        this.bit = bit;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public Double getWinRateAvg() {
        return winRateAvg;
    }

    public void setWinRateAvg(Double winRateAvg) {
        this.winRateAvg = winRateAvg;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Double getPointsAvg() {
        return pointsAvg;
    }

    public void setPointsAvg(Double pointsAvg) {
        this.pointsAvg = pointsAvg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean isFlagged) {
        this.isFlagged = isFlagged;
    }
}