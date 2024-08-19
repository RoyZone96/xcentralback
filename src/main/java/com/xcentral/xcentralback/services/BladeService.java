package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.Blade;
import com.xcentral.xcentralback.repos.BladeRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BladeService {

    @Autowired
    private BladeRepo bladeRepo;

    public List<Blade> getAllBlades() {
        return bladeRepo.findAll();
    }
}