package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.services.CourierEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*")
public class EmailTestController {

    private static final Logger logger = LoggerFactory.getLogger(EmailTestController.class);

    @Autowired
    private CourierEmailService courierEmailService;

    @Value("${courier.auth-token:}")
    private String courierAuthToken;

    @Value("${app.base-url:}")
    private String baseUrl;

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestBody Map<String, String> request) {
        logger.info("Email test endpoint called");
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email address is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Testing email sending to: {}", email);
            
            // Check Courier configuration
            response.put("courierConfigured", courierEmailService.isCourierConfigured());
            response.put("courierStatus", courierEmailService.getStatus());
            
            // Send test confirmation email
            String testToken = "test-token-" + System.currentTimeMillis();
            courierEmailService.sendConfirmationEmail(email, testToken);
            
            response.put("success", true);
            response.put("message", "Test email sent successfully");
            response.put("recipient", email);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("Test email sent successfully to: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to send test email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/email-status")
    public ResponseEntity<Map<String, Object>> getEmailStatus() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("courierConfigured", courierEmailService.isCourierConfigured());
        response.put("courierStatus", courierEmailService.getStatus());
        response.put("timestamp", System.currentTimeMillis());
        
        // Add Courier configuration info
        response.put("courierAuthTokenConfigured", courierAuthToken != null && !courierAuthToken.trim().isEmpty());
        response.put("baseUrl", baseUrl);
        response.put("emailProvider", "Courier");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/simple-email")
    public ResponseEntity<Map<String, Object>> sendSimpleTestEmail(@RequestBody Map<String, String> request) {
        logger.info("Simple email test endpoint called");
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email address is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Testing simple email sending to: {}", email);
            
            // Send simple test email
            courierEmailService.sendTestEmail(email);
            
            response.put("success", true);
            response.put("message", "Simple test email sent successfully");
            response.put("recipient", email);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("Simple test email sent successfully to: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to send simple test email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
