package com.xcentral.xcentralback.repos;

import java.util.Optional;
import java.util.List;
import com.xcentral.xcentralback.models.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findById(long id);

    @Transactional
    @Modifying
    @Query("Update User u set u.password = :password where u.email = :email")
    void updatePassword(String email, String password);
}