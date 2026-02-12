package com.fixora.maintainance.user.infrastructure.notification;

import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock email notification service implementation.
 * This is a placeholder that doesn't actually send emails.
 * 
 * NOTE: This is kept for development/testing. In production, use EmailNotificationService.
 * To switch to real emails, remove @Service from this class and ensure EmailNotificationService
 * is the only implementation of INotificationService.
 */
// @Service  // Commented out - use EmailNotificationService instead
public class MockEmailNotificationService implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(MockEmailNotificationService.class);

    @Override
    public void sendNotification(NotificationRequest notificationRequest) {
        // Mock implementation - just log the notification
        logger.info("=== MOCK EMAIL NOTIFICATION ===");
        logger.info("To: {}", notificationRequest.getRecipientEmail());
        logger.info("Subject: {}", notificationRequest.getSubject());
        logger.info("Message: {}", notificationRequest.getMessage());
        if (notificationRequest.getActivationCode() != null) {
            logger.info("Activation Code: {}", notificationRequest.getActivationCode());
        }
        logger.info("=== END MOCK EMAIL ===");
        
        // TODO: Implement actual email sending using:
        // - JavaMailSender (Spring Mail)
        // - SendGrid API
        // - AWS SES
        // - Other email service provider
    }
}

