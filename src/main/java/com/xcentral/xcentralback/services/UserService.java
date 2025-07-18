package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.exceptions.UserNotFoundException;
import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.Verification;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.repos.VerificationRepo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private EmailService emailService;

    public String addNewUser(User user) {
        logger.info("Adding new user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        userRepo.save(user);

        String token = java.util.UUID.randomUUID().toString();
        java.util.Date expiryDate = new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 1 day from
                                                                                                          // now
        Verification verification = new Verification(
                token,
                expiryDate,
                user);
        verificationRepo.save(verification);
        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Do Not Reply-Email Verification")
                .text("To verify your email, please click the link below:\n" +
                        "http://localhost:3000/verify?token=" + token)
                .build();

        emailService.sendConfirmationEmail(mailBody, token);
        return "New user added successfully! Please check your email for verification.";
    }

    public String updateUserPassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        logger.info("Updating password for user: {}", username);
        if (newPassword == null || confirmPassword == null) {
            throw new IllegalArgumentException("New password and confirm password must not be null.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            logger.info("Password updated successfully for user: {}", username);
            return "Password updated successfully!";
        } else {
            logger.warn("Old password is incorrect for user: {}", username);
            throw new IllegalArgumentException("Old password is incorrect.");
        }
    }

    public String updateUserEmail(String username, String newEmail) {
        logger.info("Updating email for user: {}", username);
        if (userRepo.findByEmail(newEmail).isPresent()) {
            logger.warn("Email already exists: {}", newEmail);
            return "Email already exists.";
        }

        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        user.setEmail(newEmail);
        userRepo.save(user);
        logger.info("Email updated successfully for user: {}", username);
        return "Email updated successfully!";
    }

    public String resetUserPassword(String username, String newPassword) {
        logger.info("Resetting password for user: {}", username);
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        logger.info("Password reset successfully for user: {}", username);
        return "Password reset successfully!";
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(Long id) throws UserNotFoundException {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("Could not find user with id: " + id));
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with email: " + email));
    }

    public User saveOrUpdate(User user) {
        return userRepo.save(user);
    }

    public User getUserEmailAndUsernameByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with email: " + email));
        User userEmailAndUsername = new User();
        userEmailAndUsername.setEmail(user.getEmail());
        userEmailAndUsername.setUsername(user.getUsername());
        return userEmailAndUsername;
    }

    public String updateUserProfileImage(String username, String imageUrl) {
        logger.info("Updating profile image for user: {}", username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));

        user.setImageUrl(imageUrl);
        userRepo.save(user);
        logger.info("Profile image updated successfully for user: {}", username);
        return "Profile image updated successfully!";
    }

}
