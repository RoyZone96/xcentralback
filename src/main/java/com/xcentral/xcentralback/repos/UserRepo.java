package com.xcentral.xcentralback.repos;

import java.util.Optional;
import java.util.List;
import com.xcentral.xcentralback.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findById(long id);
}