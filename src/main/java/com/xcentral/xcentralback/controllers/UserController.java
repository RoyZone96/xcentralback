package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.PasswordRequest;
import com.xcentral.xcentralback.models.EmailRequest;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.RequestViaEmail;
import com.xcentral.xcentralback.services.UserService;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.repos.ForgotPasswordRepo;
import com.xcentral.xcentralback.services.AuthRequest;
import com.xcentral.xcentralback.services.JWTServices;
import com.xcentral.xcentralback.services.EmailService;
import com.xcentral.xcentralback.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.UUID;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserService userService;

    @Autowired
    JWTServices jwtServices;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/newuser")
    public String addNewUSer(@RequestBody User user) {
        logger.info("Adding new user: {}", user.getUsername());
        try {
            return userService.addNewUser(user);
        } catch (Exception e) {
            logger.error("Error adding user", e);
            throw e;
        }
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authenticating user: {}", authRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            logger.info("User authenticated: {}", authRequest.getUsername());
            return jwtServices.generateToken(authRequest.getUsername());
        } else {
            logger.warn("User not found: {}", authRequest.getUsername());
            throw new UserNotFoundException("User not found");
        }
    }

    @PutMapping("/{username}/update-password")
    public String updatePassword(@PathVariable String username, @Valid @RequestBody PasswordRequest passwordRequest) {
        logger.info("Updating password for user: {}", username);
        return userService.updateUserPassword(username, passwordRequest.getOldPassword(),
                passwordRequest.getNewPassword(),
                passwordRequest.getConfirmPassword());
    }

    @PutMapping("/users/{username}/update-email")
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
}
