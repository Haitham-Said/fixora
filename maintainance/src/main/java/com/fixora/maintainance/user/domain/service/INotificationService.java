package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.NotificationRequest;

/**
 * Abstraction for notification services (Email, SMS, Push, etc.)
 * Implementations can be swapped without changing business logic
 */
public interface INotificationService {
    
    /**
     * Sends a notification
     * @param notificationRequest The notification request containing recipient, type, and content
     */
    void sendNotification(NotificationRequest notificationRequest);
}

