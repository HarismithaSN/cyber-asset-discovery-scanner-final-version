package com.internship.tool.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of EmailService using JavaMailSender and Thymeleaf templates.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@cyberasset.com}")
    private String fromAddress;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email sent successfully to {} with subject: {}", to, subject);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendDailyReminder(String to, String username, Map<String, Long> assetStats) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("totalAssets", assetStats.getOrDefault("total", 0L));
        variables.put("activeAssets", assetStats.getOrDefault("ACTIVE", 0L));
        variables.put("inactiveAssets", assetStats.getOrDefault("INACTIVE", 0L));
        variables.put("unknownAssets", assetStats.getOrDefault("UNKNOWN", 0L));
        variables.put("decommissionedAssets", assetStats.getOrDefault("DECOMMISSIONED", 0L));

        sendHtmlEmail(to, "🔒 Daily Cyber Asset Summary", "daily-reminder", variables);
    }

    @Override
    public void sendDeadlineAlert(String to, String username, int criticalAssetCount, List<String> criticalAssetNames) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("criticalCount", criticalAssetCount);
        variables.put("criticalAssetNames", criticalAssetNames);

        sendHtmlEmail(to, "⚠️ Critical Asset Deadline Alert", "deadline-alert", variables);
    }
}
