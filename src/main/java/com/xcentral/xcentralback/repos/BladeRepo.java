package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Blade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BladeRepo extends JpaRepository<Blade, Long> {
    List<Blade> findAll();
}
