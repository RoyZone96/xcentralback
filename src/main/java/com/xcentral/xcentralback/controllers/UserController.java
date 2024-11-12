package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.PasswordRequest;
import com.xcentral.xcentralback.models.EmailRequest;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.services.UsersService;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.services.AuthRequest;
import com.xcentral.xcentralback.services.JWTServices;
import com.xcentral.xcentralback.exceptions.UserNotFoundException;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UsersService usersService;

    @Autowired
    JWTServices jwtServices;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/users/newuser")
    public String addNewUSer(@RequestBody User user) {
        return usersService.addNewUser(user);
    }

    @PostMapping("/users/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtServices.generateToken(authRequest.getUsername());
        } else {
            throw new UserNotFoundException("User not found");
        }
    }
    @PutMapping("/users/{username}/update-password")
    public String updatePassword(@PathVariable String username, @Valid @RequestBody PasswordRequest passwordRequest) {
        return usersService.updateUserPassword(username, passwordRequest.getOldPassword(), passwordRequest.getNewPassword(),
                passwordRequest.getConfirmPassword());
    }

    @PutMapping("/users/{username}/update-email")
    public String updateEmail(@PathVariable String username, @RequestBody EmailRequest emailUpdateRequest) {
        return usersService.updateUserEmail(username, emailUpdateRequest.getNewEmail());
    }

    @PutMapping("/users/{username}/reset-password")
    public String resetPassword(@PathVariable String username, @RequestBody PasswordRequest passwordRequest) {
        return usersService.resetUserPassword(username, passwordRequest.getNewPassword());
    }

    @GetMapping("/users/userlist")
    public List<User> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/users/id/{id}")
    public User getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @GetMapping("/users/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return usersService.getUserByUsername(username);
    }

    @GetMapping("/users/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return usersService.getUserByEmail(email);
    }
}
