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

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private UserRepo userRepo;

    public void sendConfirmationEmail(MailBody mailBody, String token) {
        logger.info("EmailService.sendConfirmationEmail called for: {}", mailBody.getTo());
        logger.info("Token: {}", token);
        logger.info("Base URL: {}", baseUrl);
        
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            logger.info("User found in database, proceeding to send email");
            try {
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
            } catch (Exception e) {
                logger.error("Exception in sendConfirmationEmail: {}", e.getMessage(), e);
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
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(mailBody.getTo());
                message.setFrom("xcentralmail@gmail.com");
                message.setSubject("Do Not Reply-Password Reset Request");
                message.setText("To reset your password, please click the link below and enter the OTP:" + otp + "\n" +
                        frontendUrl + "/otpEntry");
                javaMailSender.send(message);
                logger.info("Password reset email sent successfully to: {}", mailBody.getTo());
            } catch (Exception e) {
                logger.error("Failed to send password reset email to {}: {}", mailBody.getTo(), e.getMessage());
                throw new RuntimeException("Failed to send password reset email", e);
            }
        } else {
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

}
