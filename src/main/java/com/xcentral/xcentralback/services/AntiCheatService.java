package com.xcentral.xcentralback.services;

import com.xcentral.xcentralback.models.Submission;
import com.xcentral.xcentralback.repos.SubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class AntiCheatService {

    private static final Logger logger = LoggerFactory.getLogger(AntiCheatService.class);

    @Autowired
    private SubRepo subRepo;

    // Threshold values for suspicious activity
    private static final int SUSPICIOUS_WINS_THRESHOLD = 50; // Flag if more than 50 wins in time period
    private static final int TIME_PERIOD_HOURS = 24; // Check within last 24 hours
    private static final int RAPID_UPDATE_THRESHOLD = 10; // Flag if more than 10 updates in short time
    private static final int RAPID_UPDATE_MINUTES = 30; // Within 30 minutes

    /**
     * Check if a submission should be flagged based on anti-cheat rules
     */
    public boolean shouldFlagSubmission(Submission submission) {
        try {
            // Check for excessive wins in time period
            if (hasExcessiveWinsInTimePeriod(submission)) {
                logger.warn("Flagging submission {} for excessive wins in time period", submission.getId());
                return true;
            }

            // Check for rapid updates (multiple updates in short time)
            if (hasRapidUpdates(submission)) {
                logger.warn("Flagging submission {} for rapid updates", submission.getId());
                return true;
            }

            // Check for impossible win rate patterns
            if (hasImpossibleWinRate(submission)) {
                logger.warn("Flagging submission {} for impossible win rate", submission.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error("Error checking anti-cheat rules for submission {}: {}", submission.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Check if user has excessive wins in the specified time period
     */
    private boolean hasExcessiveWinsInTimePeriod(Submission submission) {
        try {
            // Calculate date threshold (24 hours ago)
            LocalDateTime thresholdTime = LocalDateTime.now().minusHours(TIME_PERIOD_HOURS);
            Date thresholdDate = Date.from(thresholdTime.atZone(ZoneId.systemDefault()).toInstant());

            // Get all submissions for this user updated in the last 24 hours
            List<Submission> recentSubmissions = subRepo.findByUsernameAndDateUpdatedAfter(
                    submission.getUsername(), thresholdDate);

            // Calculate total wins in this period
            int totalWinsInPeriod = recentSubmissions.stream()
                    .mapToInt(Submission::getWins)
                    .sum();

            logger.info("User {} has {} total wins in last {} hours",
                    submission.getUsername(), totalWinsInPeriod, TIME_PERIOD_HOURS);

            return totalWinsInPeriod > SUSPICIOUS_WINS_THRESHOLD;
        } catch (Exception e) {
            logger.error("Error checking excessive wins: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if submission has been updated too frequently
     */
    private boolean hasRapidUpdates(Submission submission) {
        try {
            // Calculate date threshold (30 minutes ago)
            LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(RAPID_UPDATE_MINUTES);
            Date thresholdDate = Date.from(thresholdTime.atZone(ZoneId.systemDefault()).toInstant());

            // Get all submissions for this user updated in the last 30 minutes
            List<Submission> recentUpdates = subRepo.findByUsernameAndDateUpdatedAfter(
                    submission.getUsername(), thresholdDate);

            logger.info("User {} has {} updates in last {} minutes",
                    submission.getUsername(), recentUpdates.size(), RAPID_UPDATE_MINUTES);

            return recentUpdates.size() > RAPID_UPDATE_THRESHOLD;
        } catch (Exception e) {
            logger.error("Error checking rapid updates: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check for impossible win rates (e.g., 100% win rate with high number of
     * matches)
     */
    private boolean hasImpossibleWinRate(Submission submission) {
        try {
            int totalMatches = submission.getWins() + submission.getLosses();

            // If they have more than 20 matches and 100% win rate, flag as suspicious
            if (totalMatches > 20 && submission.getLosses() == 0) {
                logger.info("Flagging submission {} for impossible win rate: {}/{} ({}% win rate)",
                        submission.getId(), submission.getWins(), totalMatches,
                        totalMatches > 0 ? (submission.getWins() * 100.0 / totalMatches) : 0);
                return true;
            }

            // If they have more than 100 wins and less than 5% loss rate, flag as
            // suspicious
            if (submission.getWins() > 100 && totalMatches > 0) {
                double lossRate = (submission.getLosses() * 100.0) / totalMatches;
                if (lossRate < 5.0) {
                    logger.info("Flagging submission {} for extremely low loss rate: {}%",
                            submission.getId(), lossRate);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            logger.error("Error checking impossible win rate: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Apply anti-cheat check to a submission and flag if necessary
     */
    public void checkAndFlagSubmission(Submission submission) {
        try {
            if (shouldFlagSubmission(submission)) {
                submission.setFlagged(true);
                logger.warn("Submission {} has been flagged for suspicious activity", submission.getId());
            } else {
                // If it was previously flagged but now passes checks, unflag it
                if (submission.isFlagged()) {
                    submission.setFlagged(false);
                    logger.info("Submission {} has been unflagged", submission.getId());
                }
            }
        } catch (Exception e) {
            logger.error("Error in checkAndFlagSubmission for submission {}: {}", submission.getId(), e.getMessage());
        }
    }
}