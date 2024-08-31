package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.Ratchet;
import com.xcentral.xcentralback.repos.RatchetRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatchetService {

    @Autowired
    private RatchetRepo ratchetRepo;

    public List<Ratchet> getAllRatchets() {
        return ratchetRepo.findAll();
    }
}