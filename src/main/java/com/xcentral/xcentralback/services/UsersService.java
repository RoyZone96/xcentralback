package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.exceptions.UserNotFoundException;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.repos.UserRepo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@Transactional
public class UsersService {

    @Autowired
    UserRepo userRepo;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public String addNewUser(User user) {
        System.out.println("The method did get called, yo");
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "New user added successfully!";
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getCurrentUser() {
        // Logic to get the current user, e.g., from security context
        return new User(); // Replace with actual logic
    }

    public User getUserById(Long id) throws UserNotFoundException {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
