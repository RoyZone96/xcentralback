package com.xcentral.xcentralback.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


import com.xcentral.xcentralback.models.Bit;
import com.xcentral.xcentralback.services.BitService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/bittype")
public class BitController {

   @Autowired
    private BitService bitService;
    
    @GetMapping("/bitlist")
    public List<Bit> getAllBits() {
        return bitService.getAllBits();
    }

}
