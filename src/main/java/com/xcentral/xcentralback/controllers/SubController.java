package com.xcentral.xcentralback.controllers;

import com.xcentral.xcentralback.exceptions.SubmissionNotFoundException;
import com.xcentral.xcentralback.models.Submission;
import com.xcentral.xcentralback.services.SubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/submissions")
public class SubController {

    @Autowired
    private SubService subService;

   

    @GetMapping("/sublist")
    public List<Submission> getAllSubmissions() {
        return subService.getAllSubmissions();
    }

    @GetMapping("/{id}")
    public Submission getSubmissionById(@PathVariable Long id) throws SubmissionNotFoundException {
        return subService.getSubmissionById(id);
    }



    @GetMapping("/blade/{blade}")
    public List<Submission> getSubmissionByBlade(@PathVariable String blade) {
        return subService.getSubmissionByBlade(blade);
    }

    @GetMapping("/ratchet/{ratchet}")
    public List<Submission> getSubmissionByRachet(@PathVariable String rachet) {
        return subService.getSubmissionByRachet(rachet);
    }

    @GetMapping("/bit/{bit}")
    public List<Submission> getSubmissionByBit(@PathVariable String bit) {
        return subService.getSubmissionByBit(bit);
    }

    @GetMapping("/wins/{wins}")
    public List<Submission> getSubmissionByWins(@PathVariable int wins) {
        return subService.getSubmissionByWins(wins);
    }

    @GetMapping("/losses/{losses}")
    public List<Submission> getSubmissionByLosses(@PathVariable int losses) {
        return subService.getSubmissionByLosses(losses);
    }

    @GetMapping("/winRateAvg/{winRateAvg}")
    public List<Submission> getSubmissionByWinRateAvg(@PathVariable double winRateAvg) {
        return subService.getSubmissionByWinRateAvg(winRateAvg);
    }

    @GetMapping("/dateCreated/{dateCreated}")
    public List<Submission> getSubmissionByDateCreated(@PathVariable Date dateCreated) {
        return subService.getSubmissionByDateCreated(dateCreated);
    }

    @GetMapping("/dateUpdated/{dateUpdated}")
    public List<Submission> getSubmissionByDateUpdated(@PathVariable Date dateUpdated) {
        return subService.getSubmissionByDateUpdated(dateUpdated);
    }

    @GetMapping("sublist/user_id/{user_id}")
    public List<Submission> getSubmissionsByUserId(@PathVariable("user_id") long userId) {
        return subService.getSubmissionsByUserId(userId);
    }

     @PostMapping("/newsub")
    public String addNewSubmission(@RequestBody Submission submission) {
        return subService.addNewSubmission(submission);
    }

    @DeleteMapping("/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        subService.deleteSubmission(id);
    }

    @PutMapping("/{id}")
    public void updateSubmission(@PathVariable Long id, @RequestBody Submission updatedSubmission) throws SubmissionNotFoundException {
        subService.updateSubmission(id, updatedSubmission);
    }
}