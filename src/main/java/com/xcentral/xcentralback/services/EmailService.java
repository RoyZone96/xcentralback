package com.xcentral.xcentralback.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * This package contains the service classes for the XCentralBack application.
 * 
 * The EmailService class within this package is responsible for handling 
 * email-related operations such as sending and receiving emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, please click the link below:\n" + 
                        "http://localhost:8080/reset-password?token=" + token);
        emailSender.send(message);
    }
}

