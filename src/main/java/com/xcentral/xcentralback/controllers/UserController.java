package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.services.UsersService;
import com.xcentral.xcentralback.exceptions.GlobalExceptionHandler;
import com.xcentral.xcentralback.repos.UserRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    UsersService usersService;

    @PostMapping("/newuser")
    public String addNewUSer(@RequestBody User user) {
        return usersService.addNewUser(user);
    }
    

    @GetMapping("/allusers")
    public List<User> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public User getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return usersService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return usersService.getUserByEmail(email);
    }
}
