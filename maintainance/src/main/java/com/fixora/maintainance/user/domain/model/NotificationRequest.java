package com.fixora.maintainance.user.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String recipientEmail;
    private String recipientName;
    private NotificationType notificationType;
    private String subject;
    private String message;
    private String activationCode; // For activation code notifications
}

