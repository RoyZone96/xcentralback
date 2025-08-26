package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private CourierEmailService courierEmailService;

    @Autowired
    private UserRepo userRepo;

    public void sendConfirmationEmail(MailBody mailBody, String token) {
        logger.info("EmailService.sendConfirmationEmail called for: {}", mailBody.getTo());
        logger.info("Token: {}", token);
        
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            logger.info("User found in database, proceeding to send confirmation email");
            
            try {
                courierEmailService.sendConfirmationEmail(mailBody.getTo(), token);
                logger.info("Confirmation email sent successfully via Courier to: {}", mailBody.getTo());
            } catch (Exception e) {
                logger.error("Failed to send confirmation email: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to send confirmation email", e);
            }
        } else {
            logger.error("User with email {} not found in database for confirmation email", mailBody.getTo());
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

    public void sendPasswordResetEmail(MailBody mailBody, int otp) {
        logger.info("Attempting to send password reset email to: {}", mailBody.getTo());
        
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            try {
                courierEmailService.sendPasswordResetEmail(mailBody.getTo(), otp);
                logger.info("Password reset email sent successfully via Courier to: {}", mailBody.getTo());
            } catch (Exception e) {
                logger.error("Failed to send password reset email to {}: {}", mailBody.getTo(), e.getMessage(), e);
                throw new RuntimeException("Failed to send password reset email", e);
            }
        } else {
            logger.error("User with email {} not found in database for password reset", mailBody.getTo());
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

    public void sendTestEmail(String email) {
        logger.info("=== EMAIL TEST STARTING ===");
        logger.info("Sending test email to: {}", email);
        
        try {
            courierEmailService.sendTestEmail(email);
            logger.info("=== TEST EMAIL SENT SUCCESSFULLY VIA COURIER ===");
        } catch (Exception e) {
            logger.error("=== TEST EMAIL FAILED ===");
            logger.error("Error details: {}", e.getMessage(), e);
            throw new RuntimeException("Test email failed: " + e.getMessage(), e);
        }
    }

    public boolean isCourierConfigured() {
        return courierEmailService.isCourierConfigured();
    }
    
    public String getCourierStatus() {
        return courierEmailService.getStatus();
    }
}
