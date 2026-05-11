package com.internship.tool.service;

import java.util.Map;

/**
 * Service interface for sending email notifications.
 */
public interface EmailService {

    /**
     * Sends an HTML email using a Thymeleaf template.
     *
     * @param to           recipient email address
     * @param subject      email subject
     * @param templateName Thymeleaf template name (without extension)
     * @param variables    template variables
     */
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Sends a daily reminder email with asset statistics.
     */
    void sendDailyReminder(String to, String username, Map<String, Long> assetStats);

    /**
     * Sends a deadline alert email for critical/high-risk assets.
     */
    void sendDeadlineAlert(String to, String username, int criticalAssetCount, java.util.List<String> criticalAssetNames);
}
