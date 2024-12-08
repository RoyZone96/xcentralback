package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.repos.UserRepo;
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

    @Autowired
    private UserRepo userRepo;

    public void sendPasswordResetEmail(String to, String subject, String token) {
        if (userRepo.findByEmail(to).isPresent()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, please click the link below:\n" +
                    "http://localhost:8080/resetpassword?token=" + token);
            emailSender.send(message);
        } else {
            throw new IllegalArgumentException("User with email " + to + " does not exist.");
        }

    }
}
