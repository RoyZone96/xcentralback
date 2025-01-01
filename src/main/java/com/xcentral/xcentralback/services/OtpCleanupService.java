package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.repos.ForgotPasswordRepo;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class OtpCleanupService {

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    @Scheduled(fixedRate = 800000) // Run every 30 minutes
    @Transactional
    public void cleanUpExpiredOtps() {
        java.sql.Date now = new java.sql.Date(Instant.now().toEpochMilli());
        forgotPasswordRepo.deleteAllByExpiryTimeBefore(now);
        System.out.println("Expired OTPs cleaned up at: " + now);
    }
}