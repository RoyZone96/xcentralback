package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private UserRepo userRepo;

    public void sendConfirmationEmail(MailBody mailBody, String token) {
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailBody.getTo());
            message.setFrom("donotreplyxcentral@gmail.com");
            message.setSubject("Do Not Reply - Email Confirmation");
            String confirmationLink = baseUrl + "/users/confirm?token=" + token;
            message.setText("Thank you for registering! Please confirm your email by clicking the link below:\n"
                    + confirmationLink + "\nIf you did not request this, please ignore this email.");
            javaMailSender.send(message);
        } else {
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

    public void sendPasswordResetEmail(MailBody mailBody, int otp) {
        if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailBody.getTo());
            message.setFrom("donotreplyxcentral@gmail.com");
            message.setSubject("Do Not Reply-Password Reset Request");
            message.setText("To reset your password, please click the link below and enter the OTP:" + otp + "\n" +
                    frontendUrl + "/otpEntry");
            javaMailSender.send(message);
        } else {
            throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
        }
    }

}
