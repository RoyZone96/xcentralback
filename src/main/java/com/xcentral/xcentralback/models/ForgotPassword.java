package com.xcentral.xcentralback.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Data
public class ForgotPassword {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fpId;

    @Column(nullable = false, unique = true)
    private long otp;

    @Column(nullable = false)
    private Date expiryTime;

    @OneToOne
    private User user;


}
