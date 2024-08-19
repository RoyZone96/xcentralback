package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.Rachet;
import com.xcentral.xcentralback.repos.RachetRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RachetService {

    @Autowired
    private RachetRepo rachetRepo;

    public List<Rachet> getAllRachets() {
        return rachetRepo.findAll();
    }
}