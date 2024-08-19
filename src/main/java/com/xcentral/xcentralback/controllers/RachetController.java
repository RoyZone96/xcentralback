package com.xcentral.xcentralback.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.xcentral.xcentralback.models.Rachet;
import com.xcentral.xcentralback.services.RachetService;

@RestController
@RequestMapping("/rachets")
public class RachetController {
    
@Autowired
private RachetService rachetService;

@GetMapping("/rachetlist")
public List<Rachet> getAllRachets() {
    return rachetService.getAllRachets();
}
}