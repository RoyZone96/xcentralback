package com.xcentral.xcentralback.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@CrossOrigin(origins = "*") // Temporary debug - allow all origins
public class DebugController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.frontend-url:not-set}")
    private String frontendUrl;

    @Value("${CORS_ORIGINS:not-set}")
    private String corsOrigins;

    // Add a root-level endpoint that bypasses all security
    @GetMapping("/public-health")
    public String publicHealth() {
        return "Public health check OK - " + System.currentTimeMillis();
    }

    @GetMapping("/cors-info")
    public Map<String, String> getCorsInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("activeProfile", activeProfile);
        info.put("frontendUrl", frontendUrl);
        info.put("corsOrigins", corsOrigins);
        info.put("message", "CORS debug info");
        return info;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        health.put("message", "Server is running");
        return health;
    }

    @GetMapping("/simple")
    public String simple() {
        return "Hello from xcentralback! Server is working.";
    }
}
