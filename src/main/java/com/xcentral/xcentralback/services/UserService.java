package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.exceptions.UserNotFoundException;
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
import org.springframework.beans.factory.annotation.Value;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private CourierEmailService courierEmailService;

    public String addNewUser(User user) {
        logger.info("Adding new user: {}", user.getUsername());

        try {
            if (userRepo.findByUsername(user.getUsername()).isPresent()) {
                logger.warn("Username already exists: {}", user.getUsername());
                return "Username already exists.";
            }

            if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                logger.warn("Email already exists: {}", user.getEmail());
                return "Email already exists.";
            }

            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            user.setEnabled(false);
            userRepo.save(user);

            String token = java.util.UUID.randomUUID().toString();
            java.util.Date expiryDate = new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 1 day
                                                                                                              // from
                                                                                                              // now
            Verification verification = new Verification(
                    token,
                    expiryDate,
                    user);
            verificationRepo.save(verification);

            try {
                logger.info("Attempting to send confirmation email to: {}", user.getEmail());
                logger.info("CourierEmailService instance: {}", courierEmailService != null ? "NOT NULL" : "NULL");
                courierEmailService.sendConfirmationEmail(user.getEmail(), token);
                logger.info("Confirmation email sent successfully to: {}", user.getEmail());
            } catch (Exception emailException) {
                logger.error("Failed to send confirmation email to {}: {}", user.getEmail(),
                        emailException.getMessage(), emailException);
                logger.error("Exception class: {}", emailException.getClass().getName());
                // Don't fail the entire registration if email fails
                return "User registered successfully, but email confirmation failed. Please contact support.";
            }

            return "New user added successfully! Please check your email for verification.";
        } catch (IllegalArgumentException e) {
            logger.error("Error adding new user: {}", e.getMessage());
            return "Error adding new user: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error while adding new user: {}", e.getMessage(), e);
            return "Unexpected error while adding new user: " + e.getMessage();
        }
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
