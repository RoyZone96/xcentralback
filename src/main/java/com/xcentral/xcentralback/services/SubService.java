package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.exceptions.SubmissionNotFoundException;
import com.xcentral.xcentralback.repos.SubRepo;
import com.xcentral.xcentralback.models.Submission;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Date;

@Service
public class SubService {

    @Autowired
    SubRepo subRepo;

     public String addNewSubmission(Submission submission) {
        System.out.println("The method did get called, yo");
        try {
            subRepo.save(submission);
            return "New submission added successfully!";
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                // Handle the duplicate entry case
                System.out.println("Duplicate entry detected: " + e.getMessage());
                return "Duplicate entry detected. Submission not added.";
            } else {
                // Re-throw the exception if it's not a duplicate entry issue
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

    public List<Submission> getSubmissionsByUserId(Long userId) {
        return subRepo.findByUserId(userId);
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
        existingSubmission.setWins(updatedSubmission.getWins());
        existingSubmission.setLosses(updatedSubmission.getLosses());
        subRepo.save(existingSubmission);
    }
}