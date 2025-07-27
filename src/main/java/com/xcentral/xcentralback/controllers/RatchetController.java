package com.xcentral.xcentralback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.xcentral.xcentralback.models.Ratchet;
import com.xcentral.xcentralback.services.RatchetService;

@RestController
@RequestMapping("/ratchets")
public class RatchetController {

    @Autowired
    private RatchetService ratchetService;

    @GetMapping("/ratchetlist")
    public List<Ratchet> getAllRatchets() {
        return ratchetService.getAllRatchets();
    }
}