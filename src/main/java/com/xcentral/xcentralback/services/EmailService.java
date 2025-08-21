package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.courier.api.Courier;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${courier.auth-token:}")
    private String courierAuthToken;

    private Courier courier;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private UserRepo userRepo;

    private Courier getCourierClient() {
        if (courier == null && courierAuthToken != null && !courierAuthToken.trim().isEmpty()) {
            courier = Courier.builder()
                .authorizationToken(courierAuthToken)
                .build();
        }
        return courier;
    }

    public void sendConfirmationEmail(MailBody mailBody, String token) {
        logger.info("EmailService.sendConfirmationEmail called for: {}", mailBody.getTo());
        logger.info("Token: {}", token);
        logger.info("Base URL: {}", baseUrl);
        
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            logger.info("User found in database, proceeding to send email");
            
            // Try Courier first if available
            if (courierAuthToken != null && !courierAuthToken.trim().isEmpty()) {
                try {
                    sendConfirmationEmailWithCourier(mailBody, token);
                    logger.info("Confirmation email sent successfully via Courier to: {}", mailBody.getTo());
                    return;
                } catch (Exception e) {
                    logger.warn("Failed to send via Courier, falling back to Spring Mail: {}", e.getMessage());
                }
            }
            
            // Fallback to Spring Mail
            try {
                sendConfirmationEmailWithSpringMail(mailBody, token);
                logger.info("Confirmation email sent successfully via Spring Mail to: {}", mailBody.getTo());
            } catch (Exception e) {
                logger.error("Exception in sendConfirmationEmail: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to send confirmation email", e);
            }
        } else {
            logger.error("User with email {} not found in database for confirmation email", mailBody.getTo());
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

    private void sendConfirmationEmailWithCourier(MailBody mailBody, String token) {
        logger.info("Attempting to send confirmation email via Courier");
        Courier courierClient = getCourierClient();
        if (courierClient == null) {
            throw new RuntimeException("Courier client not initialized");
        }
        
        // For now, using a simple text email. You can create templates in Courier dashboard later
        try {
            // Note: This is a simplified version. You'll need to set up proper templates in Courier
            // For now, we'll use Spring Mail as the primary method
            throw new RuntimeException("Courier integration pending - template setup required");
        } catch (Exception e) {
            logger.error("Courier send failed: {}", e.getMessage());
            throw e;
        }
    }

    private void sendConfirmationEmailWithSpringMail(MailBody mailBody, String token) {
        logger.info("Attempting to send confirmation email via Spring Mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.getTo());
        message.setFrom("xcentralmail@gmail.com");
        message.setSubject("Do Not Reply - Email Confirmation");
        String confirmationLink = baseUrl + "/users/confirm?token=" + token;
        logger.info("Confirmation link: {}", confirmationLink);
        message.setText("Thank you for registering! Please confirm your email by clicking the link below:\n"
                + confirmationLink + "\nIf you did not request this, please ignore this email.");
        
        logger.info("About to send email via JavaMailSender");
        javaMailSender.send(message);
        logger.info("Email sent successfully via JavaMailSender");
    }

    public void sendPasswordResetEmail(MailBody mailBody, int otp) {
        logger.info("Attempting to send password reset email to: {}", mailBody.getTo());
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(mailBody.getTo());
                message.setFrom("xcentralmail@gmail.com");
                message.setSubject("Do Not Reply-Password Reset Request");
                message.setText("To reset your password, please click the link below and enter the OTP:" + otp + "\n" +
                        frontendUrl + "/otpEntry");
                javaMailSender.send(message);
                logger.info("Password reset email sent successfully to: {}", mailBody.getTo());
            } catch (Exception e) {
                logger.error("Failed to send password reset email to {}: {}", mailBody.getTo(), e.getMessage(), e);
                throw new RuntimeException("Failed to send password reset email", e);
            }
        } else {
            logger.error("User with email {} not found in database for password reset", mailBody.getTo());
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

}
