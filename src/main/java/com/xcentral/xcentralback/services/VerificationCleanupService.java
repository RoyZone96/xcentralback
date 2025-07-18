package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.repos.VerificationRepo;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class VerificationCleanupService {

    @Autowired
    private VerificationRepo verificationRepo;

    @Scheduled(fixedRate = 1800000) // Run every 30 minutes (same as OTP cleanup)
    @Transactional
    public void cleanUpExpiredVerifications() {
        java.sql.Date now = new java.sql.Date(Instant.now().toEpochMilli());
        verificationRepo.deleteAllByExpiryDateBefore(now);
        System.out.println("Expired verifications cleaned up at: " + now);
    }
}
