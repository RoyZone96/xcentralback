package com.xcentral.xcentralback.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xcentral.xcentralback.models.ForgotPassword;
import com.xcentral.xcentralback.models.ChangePassword;
import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.models.User;

import com.xcentral.xcentralback.repos.ForgotPasswordRepo;
import com.xcentral.xcentralback.repos.UserRepo;

import com.xcentral.xcentralback.services.EmailService;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address: " + email);
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " does not exist."));

        int otp = otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("OTP for password reset")
                .text("Here is the OTP to reset your password: " + otp)
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expiryTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Set expiry time to 1 hour
                .user(user)
                .build();

        forgotPasswordRepo.save(fp);
        emailService.sendPasswordResetEmail(mailBody, otp);

        return ResponseEntity.ok("OTP sent to email: " + email);
    }

    @PostMapping("/verifyOTP")
    @Transactional
    public ResponseEntity<String> verifyOTP(@RequestBody Map<String, Integer> requestBody) {
        int otp = requestBody.get("otp");
        ForgotPassword fp = forgotPasswordRepo.findByOtp(otp)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid OTP"));

        // Check if the OTP has expired
        if (fp.getExpiryTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepo.deleteByFpId(fp.getFpId());
            entityManager.flush();
            entityManager.clear();
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/resetPassword")
    @Transactional
    public ResponseEntity<String> resetPasswordHandler(@RequestBody ChangePassword changePassword) {
        Optional<ForgotPassword> forgotPasswordOpt = forgotPasswordRepo.findByOtp(changePassword.getOtp());
        if (forgotPasswordOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
        }

        ForgotPassword forgotPassword = forgotPasswordOpt.get();

        String newPassword = changePassword.getNewPassword();
        String confirmPassword = changePassword.getConfirmPassword();
        if (newPassword == null || confirmPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password fields cannot be null.");
        }
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match.");
        }

        User user = forgotPassword.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found for this OTP.");
        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepo.save(user);
        forgotPasswordRepo.deleteByOtp(forgotPassword.getOtp());
        entityManager.flush();
        entityManager.clear();

        return ResponseEntity.ok("Password reset successful.");
    }

    private int otpGenerator() {
        SecureRandom secRandom = new SecureRandom();
        return secRandom.nextInt(900000) + 100000; // Generates a 6-digit OTP
    }
}