package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.PasswordRequest;
import com.xcentral.xcentralback.models.EmailRequest;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.Verification;
import com.xcentral.xcentralback.services.UserService;
import com.xcentral.xcentralback.services.FileUploadService;
import com.xcentral.xcentralback.services.EmailService;
import com.xcentral.xcentralback.models.MailBody;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.repos.VerificationRepo;
import com.xcentral.xcentralback.services.AuthRequest;
import com.xcentral.xcentralback.services.JWTServices;
import com.xcentral.xcentralback.exceptions.UserNotFoundException;
import com.xcentral.xcentralback.services.PasswordService;

import org.springframework.http.ResponseEntity;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    UserRepo userRepo;

    @Autowired
    VerificationRepo verificationRepo;

    @Autowired
    UserService userService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    EmailService emailService;

    @Autowired
    JWTServices jwtServices;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordService passwordService;

    @PostMapping("/newuser")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        logger.info("Adding new user: {}", user.getUsername());
        try {
            if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username, email, and password are required.");
            }
            // Check if username already exists
            if (userRepo.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already exists. Please choose a different username.");
            }

            // Check if email already exists
            if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists. Please use a different email.");
            }

            String result = userService.addNewUser(user);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // Password validation errors from PasswordService
            logger.warn("Password validation failed for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Password validation failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        logger.info("Confirming email with token: {}", token);

        Verification verification = verificationRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (verification.getExpiryDate().before(new java.util.Date())) {
            logger.warn("Token expired for token: {}", token);
            // Delete expired verification
            verificationRepo.delete(verification);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        User user = verification.getUser();
        user.setEnabled(true);
        userRepo.save(user);

        // Delete used verification token
        verificationRepo.delete(verification);
        logger.info("Email confirmed successfully for user: {}", user.getUsername());

        return ResponseEntity.ok("Email confirmed successfully!");
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authenticating user: {}", authRequest.getUsername());
        Optional<User> userOptional = userRepo.findByUsername(authRequest.getUsername());
        if (userOptional.isEmpty()) {
            logger.warn("User not found: {}", authRequest.getUsername());
            throw new UserNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (!user.isEnabled()) {
            logger.warn("Account disabled for user: {}", authRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is not enabled. Please verify your email.");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            logger.info("User authenticated: {}", authRequest.getUsername());
            return jwtServices.generateToken(user);
        } else {
            logger.warn("User not found: {}", authRequest.getUsername());
            throw new UserNotFoundException("User not found");
        }
    }

    @PutMapping("/{id}/toggleAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleAdmin(@PathVariable Long id) {
        logger.info("Toggling admin status for user with ID {}", id);
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            logger.error("User with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOptional.get();

        // Toggle between ADMIN and USER roles
        if ("ADMIN".equals(user.getRole())) {
            user.setRole("USER");
            userRepo.save(user);
            logger.info("User with ID {} demoted to USER", id);
            return ResponseEntity.ok("User with ID " + id + " has been demoted to USER");
        } else {
            user.setRole("ADMIN");
            userRepo.save(user);
            logger.info("User with ID {} promoted to ADMIN", id);
            return ResponseEntity.ok("User with ID " + id + " has been promoted to ADMIN");
        }
    }

    @PutMapping("/{username}/update-password")
    public String updatePassword(@PathVariable String username, @Valid @RequestBody PasswordRequest passwordRequest) {
        logger.info("Updating password for user: {}", username);
        return userService.updateUserPassword(username, passwordRequest.getOldPassword(),
                passwordRequest.getNewPassword(),
                passwordRequest.getConfirmPassword());
    }

    @PutMapping("/{username}/update-email")
    public String updateEmail(@PathVariable String username, @Valid @RequestBody EmailRequest emailUpdateRequest) {
        logger.info("Updating email for user: {}", username);
        return userService.updateUserEmail(username, emailUpdateRequest.getNewEmail());
    }

    @GetMapping("/userlist")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/{username}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Uploading profile image for user: {}", username);

            // Validate user exists
            userService.getUserByUsername(username);

            // Upload file and get the path
            String imagePath = fileUploadService.uploadProfileImage(file, username);

            // Update user's image URL in database
            userService.updateUserProfileImage(username, imagePath);

            return ResponseEntity.ok("Profile image uploaded successfully. Path: " + imagePath);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            logger.error("Error uploading profile image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<String> getProfilePictureUrl(@PathVariable String username) {
        try {
            logger.info("Getting profile picture URL for user: {}", username);

            User user = userService.getUserByUsername(username);
            String imageUrl = user.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Return the full URL for the frontend
                String fullUrl = baseUrl + imageUrl;
                return ResponseEntity.ok(fullUrl);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            logger.error("Error getting profile picture URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting profile picture URL");
        }
    }

    @GetMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmationEmail(@RequestParam String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("User is already confirmed.");
        }
        // Generate a new token and expiry (or reuse existing if you prefer)
        String token = java.util.UUID.randomUUID().toString();
        java.util.Date expiry = new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24h
        Verification verification = new Verification(token, expiry, user);
        verificationRepo.save(verification);
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("Email Confirmation")
                .text("") // Not used, see EmailService
                .build();
        emailService.sendConfirmationEmail(mailBody, token);
        return ResponseEntity.ok("Confirmation email resent.");
    }

    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        try {
            if (username != null) {
                boolean usernameExists = userRepo.findByUsername(username).isPresent();
                return ResponseEntity.ok(Map.of("field", "username", "available", !usernameExists));
            }

            if (email != null) {
                boolean emailExists = userRepo.findByEmail(email).isPresent();
                return ResponseEntity.ok(Map.of("field", "email", "available", !emailExists));
            }

            return ResponseEntity.badRequest().body("Either username or email parameter is required");
        } catch (Exception e) {
            logger.error("Error checking availability", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking availability");
        }
    }
}
