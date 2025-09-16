package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.models.Blade;
import com.xcentral.xcentralback.models.Ratchet;
import com.xcentral.xcentralback.models.Bit;
import com.xcentral.xcentralback.models.User;
import com.xcentral.xcentralback.models.Submission;
import com.xcentral.xcentralback.repos.BladeRepo;
import com.xcentral.xcentralback.repos.RatchetRepo;
import com.xcentral.xcentralback.repos.BitRepo;
import com.xcentral.xcentralback.repos.UserRepo;
import com.xcentral.xcentralback.services.SubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private BladeRepo bladeRepo;
    @Autowired
    private RatchetRepo ratchetRepo;
    @Autowired
    private BitRepo bitRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SubService subService;

    // Add new Blade
    @PostMapping("/blade")
    public ResponseEntity<?> addBlade(@RequestBody Blade blade) {
        String name = blade.getBladeName();
        bladeRepo.save(blade);
        return ResponseEntity.ok("Blade added successfully: " + name);
    }

    @PutMapping("/blade/{id}")
    public ResponseEntity<?> updateBlade(@PathVariable Long id, @RequestBody Blade blade) {
        Blade existingBlade = bladeRepo.findById(id).orElse(null);
        if (existingBlade == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blade not found");
        }
        existingBlade.setBladeName(blade.getBladeName());
        bladeRepo.save(existingBlade);
        return ResponseEntity.ok("Blade updated successfully: " + existingBlade.getBladeName());
    }

    // Add new Ratchet
    @PostMapping("/ratchet")
    public ResponseEntity<?> addRatchet(@RequestBody Ratchet ratchet) {
        String name = ratchet.getRatchet();
        ratchetRepo.save(ratchet);
        return ResponseEntity.ok("Ratchet added successfully " + name);
    }

    @PutMapping("/ratchet/{id}")
    public ResponseEntity<?> updateRatchet(@PathVariable Long id, @RequestBody Ratchet ratchet) {
        Ratchet existingRatchet = ratchetRepo.findById(id).orElse(null);
        if (existingRatchet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ratchet not found");
        }
        existingRatchet.setRatchet(ratchet.getRatchet());
        ratchetRepo.save(existingRatchet);
        return ResponseEntity.ok("Ratchet updated successfully: " + existingRatchet.getRatchet());
    }

    // Add new Bit
    @PostMapping("/bit")
    public ResponseEntity<?> addBit(@RequestBody Bit bit) {
        String name = bit.getBit();
        bitRepo.save(bit);
        return ResponseEntity.ok("Bit added successfully: " + name);
    }

    @PutMapping("/bit/{id}")
    public ResponseEntity<?> updateBit(@PathVariable Long id, @RequestBody Bit bit) {
        Bit existingBit = bitRepo.findById(id).orElse(null);
        if (existingBit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bit not found");
        }
        existingBit.setBit(bit.getBit());
        bitRepo.save(existingBit);
        return ResponseEntity.ok("Bit updated successfully: " + existingBit.getBit());
    }

    // Toggle user account status (ban/unban)
    @PutMapping("/users/{username}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String username) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Toggle the enabled status
        if (user.isEnabled()) {
            user.setEnabled(false);
            userRepo.save(user);
            return ResponseEntity.ok("User " + username + " has been banned (disabled)");
        } else {
            user.setEnabled(true);
            userRepo.save(user);
            return ResponseEntity.ok("User " + username + " has been unbanned (enabled)");
        }
    }

    // Get all flagged submissions for admin review
    @GetMapping("/flagged-subs")
    public ResponseEntity<?> getFlaggedSubmissions() {
        try {
            List<Submission> flaggedSubmissions = subService.getFlaggedSubmissions();
            return ResponseEntity.ok(flaggedSubmissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving flagged submissions: " + e.getMessage());
        }
    }

    @GetMapping("/flagged-submissions/count")
public ResponseEntity<?> getFlaggedSubmissionsCount() {
    try {
        List<Submission> flaggedSubmissions = subService.getFlaggedSubmissions();
        return ResponseEntity.ok(flaggedSubmissions.size());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving flagged submissions count: " + e.getMessage());
    }
}


    // Resolve flagged submission (admin action)
    @PutMapping("/submissions/{id}/resolve")
    public ResponseEntity<?> resolveSubmission(@PathVariable Long id) {
        try {
            // Resolve by unflagging the submission
            subService.flagSubmission(id, false);
            return ResponseEntity.ok("Submission " + id + " has been resolved by admin");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Submission not found or error occurred: " + e.getMessage());
        }
    }

    // Get all users for admin management
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    // Get user by username
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userRepo.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user: " + e.getMessage());
        }
    }
}
