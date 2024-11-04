package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.PasswordRequest;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.services.UsersService;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.services.JWTServices;
import com.xcentral.xcentralback.AuthRequest;
import com.xcentral.xcentralback.exceptions.UserNotFoundException;

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

    @PutMapping("/users/{id}/update-password")
    public String updatePassword(@PathVariable Long id, @RequestBody PasswordRequest passwordRequest) {
        return usersService.updateUserPassword(id, passwordRequest.getOldPassword(), passwordRequest.getNewPassword(),
                passwordRequest.getConfirmPassword());
    }

    @PutMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return usersService.resetUserPassword(id, newPassword);
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
