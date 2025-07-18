package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.exceptions.SubmissionNotFoundException;
import com.xcentral.xcentralback.repos.SubRepo;
import com.xcentral.xcentralback.models.Submission;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SubService {

    private static final Logger logger = LoggerFactory.getLogger(SubService.class);

    @Autowired
    SubRepo subRepo;

    @Autowired
    AntiCheatService antiCheatService;

    public String addNewSubmission(Submission submission) {
        logger.info("Adding new submission: {}", submission);
        try {
            // Check for anti-cheat violations before saving
            antiCheatService.checkAndFlagSubmission(submission);

            subRepo.save(submission);
            logger.info("New submission added successfully!");
            return "New submission added successfully!";
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                // Handle the duplicate entry case
                logger.warn("Duplicate entry detected: {}", e.getMessage());
                return "Duplicate entry detected. Submission not added.";
            } else {
                // Re-throw the exception if it's not a duplicate entry issue
                logger.error("Data integrity violation: {}", e.getMessage());
                throw e;
            }
        }
    }

    public List<Submission> getAllSubmissions() {
        return subRepo.findAll();
    }

    public Submission getSubmissionById(Long id) throws SubmissionNotFoundException {
        return subRepo.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(id));
    }

    public List<Submission> getSubmissionsByUsername(String username) {
        return subRepo.findByUsername(username);
    }

    public List<Submission> getSubmissionByBlade(String blade) {
        return subRepo.findByBlade(blade);
    }

    public List<Submission> getSubmissionByRachet(String ratchet) {
        return subRepo.findByRatchet(ratchet);
    }

    public List<Submission> getSubmissionByBit(String bit) {
        return subRepo.findByBit(bit);
    }

    public List<Submission> getSubmissionByWins(int wins) {
        return subRepo.findByWins(wins);
    }

    public List<Submission> getSubmissionByLosses(int losses) {
        return subRepo.findByLosses(losses);
    }

    public List<Submission> getSubmissionByWinRateAvg(double winRateAvg) {
        return subRepo.findByWinRateAvg(winRateAvg);
    }

    public List<Submission> getSubmissionByDateCreated(Date dateCreated) {
        return subRepo.findByDateCreated(dateCreated);
    }

    public List<Submission> getSubmissionByDateUpdated(Date dateUpdated) {
        return subRepo.findByDateUpdated(dateUpdated);
    }

    public void deleteSubmission(Long id) {
        subRepo.deleteById(id);
    }

    public void updateSubmission(Long id, Submission updatedSubmission) throws SubmissionNotFoundException {
        Submission existingSubmission = subRepo.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(id));

        // Update the fields
        existingSubmission.setWins(updatedSubmission.getWins());
        existingSubmission.setLosses(updatedSubmission.getLosses());

        // Check for anti-cheat violations before saving
        antiCheatService.checkAndFlagSubmission(existingSubmission);

        subRepo.save(existingSubmission);
        logger.info("Submission {} updated successfully. Flagged: {}", id, existingSubmission.isFlagged());
    }

    public List<Submission> getFlaggedSubmissions() {
        return subRepo.findByIsFlagged(true);
    }

    public void flagSubmission(Long id, boolean flagged) throws SubmissionNotFoundException {
        Submission submission = subRepo.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(id));
        submission.setFlagged(flagged);
        subRepo.save(submission);
        logger.info("Submission {} {} manually", id, flagged ? "flagged" : "unflagged");
    }
}