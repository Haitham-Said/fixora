package com.fixora.maintainance.user.infrastructure.notification;

import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.model.NotificationType;
import com.fixora.maintainance.user.domain.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email Notification Service using Spring Mail
 * Supports both plain text and HTML emails
 * 
 * Configuration via application.properties:
 * - spring.mail.host (e.g., smtp.gmail.com)
 * - spring.mail.port (e.g., 587)
 * - spring.mail.username
 * - spring.mail.password
 * - spring.mail.properties.mail.smtp.auth=true
 * - spring.mail.properties.mail.smtp.starttls.enable=true
 * 
 * For free email services:
 * - Gmail: smtp.gmail.com, port 587 (requires app password)
 * - Outlook: smtp-mail.outlook.com, port 587
 * - SendGrid: smtp.sendgrid.net, port 587 (free tier available)
 */
@Service
public class EmailNotificationService implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender,
                                   @Value("${spring.mail.username:no-reply@fixora.com}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendNotification(NotificationRequest notificationRequest) {
        try {
            if (notificationRequest.getNotificationType() == NotificationType.USER_REGISTRATION_CODE ||
                notificationRequest.getNotificationType() == NotificationType.USER_ACTIVATION_CODE ||
                notificationRequest.getNotificationType() == NotificationType.TENANT_UPLOAD_CODE) {
                // Send HTML email for code notifications
                sendHtmlEmail(notificationRequest);
            } else {
                // Send plain text email
                sendPlainTextEmail(notificationRequest);
            }
            logger.info("Email sent successfully to: {}", notificationRequest.getRecipientEmail());
        } catch (Exception e) {
            logger.error("Error sending email to {}: {}", notificationRequest.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void sendPlainTextEmail(NotificationRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(request.getRecipientEmail());
        message.setSubject(request.getSubject());
        message.setText(request.getMessage());
        mailSender.send(message);
    }

    private void sendHtmlEmail(NotificationRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(request.getRecipientEmail());
        helper.setSubject(request.getSubject());

        // Build HTML email with better formatting
        String htmlContent = buildHtmlEmailContent(request);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    private String buildHtmlEmailContent(NotificationRequest request) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head><meta charset='UTF-8'></head>");
        html.append("<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        html.append("<h2 style='color: #2c3e50;'>").append(request.getSubject()).append("</h2>");
        
        // Convert plain text message to HTML (preserve line breaks)
        String htmlMessage = request.getMessage().replace("\n", "<br>");
        html.append("<div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        html.append(htmlMessage);
        html.append("</div>");
        
        // Highlight activation code if present
        if (request.getActivationCode() != null && !request.getActivationCode().isEmpty()) {
            html.append("<div style='background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center;'>");
            html.append("<p style='margin: 0; font-size: 14px; color: #666;'>Your Activation Code:</p>");
            html.append("<p style='margin: 10px 0; font-size: 24px; font-weight: bold; color: #2e7d32; letter-spacing: 2px;'>");
            html.append(request.getActivationCode());
            html.append("</p>");
            html.append("</div>");
        }
        
        html.append("<p style='color: #666; font-size: 12px; margin-top: 30px;'>");
        html.append("This is an automated message. Please do not reply to this email.");
        html.append("</p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
}

