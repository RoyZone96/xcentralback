package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.UserInfoUserDetails;
import com.xcentral.xcentralback.repos.UserRepo;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



@Component
public class UserInfoUserDetailsService implements UserDetailsService{

    @Autowired
    private  UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional <User> userInfo = repo.findByUsername(username);

       return  userInfo.map(UserInfoUserDetails::new)
       .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
            
        
    }
    
}