package com.internship.tool.service;

import com.internship.tool.entity.CyberAsset;
import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scheduled tasks for sending email notifications.
 * - Daily reminder at 8:00 AM with asset statistics
 * - Deadline alert at 9:00 AM for high-risk assets
 */
@Component
public class EmailScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);

    private final EmailService emailService;
    private final CyberAssetService assetService;
    private final UserRepository userRepository;

    @Autowired
    public EmailScheduler(EmailService emailService, CyberAssetService assetService,
                          UserRepository userRepository) {
        this.emailService = emailService;
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    /**
     * Daily reminder — runs every day at 8:00 AM.
     * Sends an asset summary email to all users with a configured email address.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReminder() {
        logger.info("Running daily reminder email job...");
        Map<String, Long> stats = assetService.getAssetStatistics();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                try {
                    emailService.sendDailyReminder(user.getEmail(), user.getUsername(), stats);
                } catch (Exception e) {
                    logger.error("Failed to send daily reminder to {}: {}", user.getEmail(), e.getMessage());
                }
            }
        }
        logger.info("Daily reminder job completed. Processed {} users.", users.size());
    }

    /**
     * Deadline alert — runs every day at 9:00 AM.
     * Alerts users about high-risk assets (risk score >= 70) that need attention.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDeadlineAlert() {
        logger.info("Running deadline alert email job...");
        List<CyberAsset> highRiskAssets = assetService.getHighRiskAssets();

        if (highRiskAssets.isEmpty()) {
            logger.info("No high-risk assets found. Skipping deadline alert.");
            return;
        }

        List<String> assetNames = highRiskAssets.stream()
                .map(a -> a.getAssetName() + " (Risk: " + a.getRiskScore() + ")")
                .collect(Collectors.toList());

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                try {
                    emailService.sendDeadlineAlert(
                            user.getEmail(), user.getUsername(),
                            highRiskAssets.size(), assetNames
                    );
                } catch (Exception e) {
                    logger.error("Failed to send deadline alert to {}: {}", user.getEmail(), e.getMessage());
                }
            }
        }
        logger.info("Deadline alert job completed. {} high-risk assets reported.", highRiskAssets.size());
    }
}
