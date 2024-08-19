package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.Bit;    
import com.xcentral.xcentralback.repos.BitRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BitService {
    

    @Autowired
    private BitRepo bitRepo;

    public List<Bit> getAllBits() {
        return bitRepo.findAll();
    }
}
