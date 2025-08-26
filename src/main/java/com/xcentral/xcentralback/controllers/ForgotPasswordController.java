package com.xcentral.xcentralback.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xcentral.xcentralback.models.ForgotPassword;
import com.xcentral.xcentralback.models.ChangePassword;
import com.xcentral.xcentralback.models.User;

import com.xcentral.xcentralback.repos.ForgotPasswordRepo;
import com.xcentral.xcentralback.repos.UserRepo;

import com.xcentral.xcentralback.services.CourierEmailService;

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
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CourierEmailService courierEmailService;

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // Simple email validation using regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        if (!isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address: " + email);
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " does not exist."));

        int otp = otpGenerator();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expiryTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Set expiry time to 1 hour
                .user(user)
                .build();

        forgotPasswordRepo.save(fp);
        courierEmailService.sendPasswordResetEmail(email, otp);

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