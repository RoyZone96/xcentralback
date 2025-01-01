package com.xcentral.xcentralback.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xcentral.xcentralback.models.ForgotPassword;
import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.models.User;

import com.xcentral.xcentralback.repos.ForgotPasswordRepo;
import com.xcentral.xcentralback.repos.UserRepo;

import com.xcentral.xcentralback.utils.ChangePassword;

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
import java.util.Objects;

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
                .expiryTime(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // Set expiry time to 10 minutes
                .user(user)
                .build();

        forgotPasswordRepo.save(fp);
        emailService.sendPasswordResetEmail(mailBody);

        return ResponseEntity.ok("OTP sent to email: " + email);
    }

    @PostMapping("/verifyOTP/{otp}/{email}")
    @Transactional
    public ResponseEntity<String> verifyOTP(@PathVariable int otp, @PathVariable String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " does not exist."));

        ForgotPassword fp = forgotPasswordRepo.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email " + email));

        if (fp.getExpiryTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepo.deleteById(fp.getFpId());
            entityManager.flush();
            entityManager.clear();
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        forgotPasswordRepo.deleteById(fp.getFpId());
        entityManager.flush();
        entityManager.clear();
        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/resetPassword/{otp}/{email}")
    @Transactional
    public ResponseEntity<String> resetPasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.confirmPassword())) {
            return new ResponseEntity<>("Passwords do not match. Please enter them again", HttpStatus.EXPECTATION_FAILED);
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " does not exist."));

        ForgotPassword fp = forgotPasswordRepo.findByOtpAndUser(changePassword.otp(), user)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email " + email));

        Date now = Date.from(Instant.now());
        System.out.println("Current time: " + now);
        System.out.println("OTP expiry time: " + fp.getExpiryTime());

        if (fp.getExpiryTime().before(now)) {
            System.out.println("OTP is expired. Deleting from database.");
            forgotPasswordRepo.deleteById(fp.getFpId());
            entityManager.flush();
            entityManager.clear();
            System.out.println("OTP deleted from database.");
            return new ResponseEntity<>("OTP has expired, please make a fresh request", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepo.updatePassword(email, encodedPassword);

        forgotPasswordRepo.deleteById(fp.getFpId());
        entityManager.flush();
        entityManager.clear();
        System.out.println("OTP deleted from database after password reset.");
        return ResponseEntity.ok("Password reset successfully");
    }

    private int otpGenerator() {
        SecureRandom secRandom = new SecureRandom();
        return secRandom.nextInt(900000) + 100000; // Generates a 6-digit OTP
    }
}