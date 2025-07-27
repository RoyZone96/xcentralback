package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.exceptions.SubmissionNotFoundException;
import com.xcentral.xcentralback.models.Submission;
import com.xcentral.xcentralback.services.SubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/submissions")
public class SubController {

    private static final Logger logger = LoggerFactory.getLogger(SubController.class);

    @Autowired
    private SubService subService;

    @GetMapping("/sublist")
    public List<Submission> getAllSubmissions() {
        logger.info("Fetching all submissions");
        return subService.getAllSubmissions();
    }

    @GetMapping("/{id}")
    public Submission getSubmissionById(@PathVariable Long id) throws SubmissionNotFoundException {
        logger.info("Fetching submission by id: {}", id);
        return subService.getSubmissionById(id);
    }

    @GetMapping("/blade/{blade}")
    public List<Submission> getSubmissionByBlade(@PathVariable String blade) {
        logger.info("Fetching submissions by blade: {}", blade);
        return subService.getSubmissionByBlade(blade);
    }

    @GetMapping("/ratchet/{ratchet}")
    public List<Submission> getSubmissionByRachet(@PathVariable String ratchet) {
        logger.info("Fetching submissions by ratchet: {}", ratchet);
        return subService.getSubmissionByRachet(ratchet);
    }

    @GetMapping("/bit/{bit}")
    public List<Submission> getSubmissionByBit(@PathVariable String bit) {
        logger.info("Fetching submissions by bit: {}", bit);
        return subService.getSubmissionByBit(bit);
    }

    @GetMapping("/wins/{wins}")
    public List<Submission> getSubmissionByWins(@PathVariable int wins) {
        logger.info("Fetching submissions by wins: {}", wins);
        return subService.getSubmissionByWins(wins);
    }

    @GetMapping("/losses/{losses}")
    public List<Submission> getSubmissionByLosses(@PathVariable int losses) {
        logger.info("Fetching submissions by losses: {}", losses);
        return subService.getSubmissionByLosses(losses);
    }

    @GetMapping("/winRateAvg/{winRateAvg}")
    public List<Submission> getSubmissionByWinRateAvg(@PathVariable double winRateAvg) {
        logger.info("Fetching submissions by win rate average: {}", winRateAvg);
        return subService.getSubmissionByWinRateAvg(winRateAvg);
    }

    @GetMapping("/dateCreated/{dateCreated}")
    public List<Submission> getSubmissionByDateCreated(@PathVariable Date dateCreated) {
        logger.info("Fetching submissions by date created: {}", dateCreated);
        return subService.getSubmissionByDateCreated(dateCreated);
    }

    @GetMapping("/dateUpdated/{dateUpdated}")
    public List<Submission> getSubmissionByDateUpdated(@PathVariable Date dateUpdated) {
        logger.info("Fetching submissions by date updated: {}", dateUpdated);
        return subService.getSubmissionByDateUpdated(dateUpdated);
    }

    @GetMapping("/sublist/username/{username}")
    public List<Submission> getSubmissionsByUsername(@PathVariable String username) {
        logger.info("Fetching submissions by username: {}", username);
        return subService.getSubmissionsByUsername(username);
    }

    @PostMapping("/newsub")
    public String addNewSubmission(@RequestBody Submission submission) {
        logger.info("Adding new submission: {}", submission);
        return subService.addNewSubmission(submission);
    }

    @DeleteMapping("/id/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        logger.info("Deleting submission with id: {}", id);
        subService.deleteSubmission(id);
    }

    @PutMapping("/id/{id}")
    public void updateSubmission(@PathVariable Long id, @RequestBody Submission updatedSubmission)
            throws SubmissionNotFoundException {
        logger.info("Updating submission with id: {}", id);
        subService.updateSubmission(id, updatedSubmission);
    }

    @GetMapping("/flagged")
    public List<Submission> getFlaggedSubmissions() {
        logger.info("Fetching all flagged submissions");
        return subService.getFlaggedSubmissions();
    }

    @PutMapping("/flag/{id}")
    public ResponseEntity<String> flagSubmission(@PathVariable Long id) {
        logger.info("Manually flagging submission with id: {}", id);
        try {
            subService.flagSubmission(id, true);
            return ResponseEntity.ok("Submission flagged successfully");
        } catch (SubmissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/unflag/{id}")
    public ResponseEntity<String> unflagSubmission(@PathVariable Long id) {
        logger.info("Manually unflagging submission with id: {}", id);
        try {
            subService.flagSubmission(id, false);
            return ResponseEntity.ok("Submission unflagged successfully");
        } catch (SubmissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}