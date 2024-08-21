package com.xcentral.xcentralback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.xcentral.xcentralback.models.Blade;
import com.xcentral.xcentralback.services.BladeService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/blade_parts")
public class BladeController {

    @Autowired
    private BladeService bladeService;

    @GetMapping("/bladelist")
    public List<Blade> getAllBlades() {
        return bladeService.getAllBlades();
    }
}