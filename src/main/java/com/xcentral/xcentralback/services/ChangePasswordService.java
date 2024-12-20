package com.xcentral.xcentralback.services;

import org.springframework.stereotype.Service;
import com.xcentral.xcentralback.utils.ChangePassword;

@Service
public class ChangePasswordService {
    public boolean changePassword(ChangePassword changePassword) {
       if(changePassword.password().equals(changePassword.confirmPassword())) {
           return true;
       }
         return false;
    }
}

