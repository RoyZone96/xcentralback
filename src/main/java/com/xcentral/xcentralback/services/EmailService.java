package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private UserRepo userRepo;

public void sendPasswordResetEmail(MailBody mailBody, int otp) {
    if (userRepo.findByEmail(mailBody.getTo()).isPresent()) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.getTo());
        message.setFrom("donotreplyxcentral@gmail.com");
        message.setSubject("Do Not Reply-Password Reset Request");
        message.setText("To reset your password, please click the link below and enter the OTP:" + otp + "\n" +
        "http://localhost:3000/otpEntry");
        javaMailSender.send(message);
    } else {
        throw new IllegalArgumentException("User with email " + mailBody.getTo() + " does not exist.");
    }
}

}
