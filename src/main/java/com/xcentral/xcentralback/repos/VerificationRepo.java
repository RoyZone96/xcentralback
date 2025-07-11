package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepo extends JpaRepository<Verification, Long> {
    Optional<Verification> findByToken(String token);
    Optional<Verification> findByUserId(Long userId);
    
}
