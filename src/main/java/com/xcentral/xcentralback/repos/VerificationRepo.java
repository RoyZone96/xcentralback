package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface VerificationRepo extends JpaRepository<Verification, Long> {
    Optional<Verification> findByToken(String token);

    Optional<Verification> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("delete from Verification v where v.expiryDate < :now")
    void deleteAllByExpiryDateBefore(Date now);
}
