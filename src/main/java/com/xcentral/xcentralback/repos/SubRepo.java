package com.xcentral.xcentralback.repos;

import com.xcentral.xcentralback.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Date;


public interface SubRepo extends JpaRepository<Submission, Long> {
    List<Submission> findAll();
    Optional<Submission> findById(Long id);
    List<Submission> findByBlade(String blade);
    List<Submission> findByRatchet(String ratchet);
    List<Submission> findByBit(String bit);
    List<Submission> findByWins(int wins);
    List<Submission> findByLosses(int losses);
    List<Submission> findByWinRateAvg(double winRateAvg);
    List<Submission> findByDateCreated(Date dateCreated);
    List<Submission> findByDateUpdated(Date dateUpdated);
    List<Submission> findByUsername(String username);
}