package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.Optional;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Integer> {
    @Query("select fp from ForgotPassword fp where fp.otp = :otp and fp.user = :user")
    Optional<ForgotPassword> findByOtpAndUser(int otp, User user);

    @Modifying
    @Query("delete from ForgotPassword fp where fp.expiryTime < current_timestamp")
    void deleteAllByExpiryTimeBefore(Date now);
}