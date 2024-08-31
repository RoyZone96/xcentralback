package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Ratchet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatchetRepo extends JpaRepository<Ratchet, Long> {
    List<Ratchet> findAll(); 
}
