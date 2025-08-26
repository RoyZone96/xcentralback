package com.xcentral.xcentralback.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class CourierEmailService {

    private static final Logger logger = LoggerFactory.getLogger(CourierEmailService.class);
    private static final String COURIER_API_URL = "https://api.courier.com/send";

    @Value("${courier.auth-token:}")
    private String courierAuthToken;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate;

    public CourierEmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isCourierConfigured() {
        return courierAuthToken != null && !courierAuthToken.trim().isEmpty();
    }

    public void sendConfirmationEmail(String recipientEmail, String token) {
        if (!isCourierConfigured()) {
            throw new RuntimeException("Courier is not configured. Please check COURIER_AUTH_TOKEN environment variable.");
        }

        logger.info("Sending confirmation email via Courier to: {}", recipientEmail);

        String confirmationLink = baseUrl + "/users/confirm?token=" + token;
        
        Map<String, Object> emailData = new HashMap<>();
        
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        to.put("email", recipientEmail);
        
        message.put("to", to);
        message.put("content", createConfirmationContent(confirmationLink));
        
        emailData.put("message", message);

        sendEmail(emailData, "confirmation email");
    }

    public void sendPasswordResetEmail(String recipientEmail, int otp) {
        if (!isCourierConfigured()) {
            throw new RuntimeException("Courier is not configured. Please check COURIER_AUTH_TOKEN environment variable.");
        }

        logger.info("Sending password reset email via Courier to: {}", recipientEmail);

        String resetLink = frontendUrl + "/otpEntry";
        
        Map<String, Object> emailData = new HashMap<>();
        
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        to.put("email", recipientEmail);
        
        message.put("to", to);
        message.put("content", createPasswordResetContent(otp, resetLink));
        
        emailData.put("message", message);

        sendEmail(emailData, "password reset email");
    }

    private Map<String, Object> createConfirmationContent(String confirmationLink) {
        Map<String, Object> content = new HashMap<>();
        content.put("title", "Email Confirmation Required");
        content.put("body", String.format(
            "Thank you for registering with XCentral!\n\n" +
            "Please confirm your email address by clicking the link below:\n%s\n\n" +
            "If you did not create this account, please ignore this email.\n\n" +
            "Best regards,\nXCentral Team",
            confirmationLink
        ));
        return content;
    }

    private Map<String, Object> createPasswordResetContent(int otp, String resetLink) {
        Map<String, Object> content = new HashMap<>();
        content.put("title", "Password Reset Request");
        content.put("body", String.format(
            "You have requested to reset your password for your XCentral account.\n\n" +
            "Your OTP (One Time Password) is: %d\n\n" +
            "Please click the link below and enter the OTP to reset your password:\n%s\n\n" +
            "This OTP will expire in 15 minutes for security reasons.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\nXCentral Team",
            otp, resetLink
        ));
        return content;
    }

    private void sendEmail(Map<String, Object> emailData, String emailType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(courierAuthToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);

            logger.info("Sending {} via Courier API", emailType);
            logger.debug("Email data: {}", emailData);

            ResponseEntity<String> response = restTemplate.exchange(
                COURIER_API_URL,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Successfully sent {} via Courier", emailType);
                logger.debug("Courier response: {}", response.getBody());
            } else {
                logger.error("Failed to send {} via Courier. Status: {}, Body: {}", 
                    emailType, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send " + emailType + " via Courier");
            }

        } catch (Exception e) {
            logger.error("Error sending {} via Courier: {}", emailType, e.getMessage(), e);
            throw new RuntimeException("Failed to send " + emailType + " via Courier: " + e.getMessage(), e);
        }
    }

    public void sendTestEmail(String recipientEmail) {
        if (!isCourierConfigured()) {
            throw new RuntimeException("Courier is not configured. Please check COURIER_AUTH_TOKEN environment variable.");
        }

        logger.info("Sending test email via Courier to: {}", recipientEmail);

        Map<String, Object> emailData = new HashMap<>();
        
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        to.put("email", recipientEmail);
        
        message.put("to", to);
        
        Map<String, Object> content = new HashMap<>();
        content.put("title", "Test Email from XCentral");
        content.put("body", String.format(
            "This is a test email to verify that Courier email integration is working correctly.\n\n" +
            "Timestamp: %s\n\n" +
            "If you received this email, the Courier integration is working properly!\n\n" +
            "Best regards,\nXCentral Team",
            java.time.LocalDateTime.now()
        ));
        
        message.put("content", content);
        emailData.put("message", message);

        sendEmail(emailData, "test email");
    }

    public String getStatus() {
        if (isCourierConfigured()) {
            return "Courier configured and ready";
        } else {
            return "Courier not configured - missing auth token";
        }
    }
}
