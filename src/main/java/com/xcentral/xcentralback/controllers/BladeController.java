package com.xcentral.xcentralback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.xcentral.xcentralback.models.Blade;
import com.xcentral.xcentralback.repos.BladeRepo;
import com.xcentral.xcentralback.services.BladeService;

@RestController
@RequestMapping("/blade_parts")
public class BladeController {

    @Autowired
    private BladeService bladeService;

    @GetMapping("/bladelist")
    public List<Blade> getAllBlades() {
        return bladeService.getAllBlades();
    }
}