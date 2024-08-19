package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Bit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BitRepo extends JpaRepository<Bit, Long> {
    List<Bit> findAll();

}