package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Rachet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RachetRepo extends JpaRepository<Rachet, Long> {
    List<Rachet> findAll(); 
}
