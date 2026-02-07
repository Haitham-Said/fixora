package com.fixora.maintainance.user.domain.model;

public enum NotificationType {
    USER_REGISTRATION_CODE,  // Code sent when user self-registers
    USER_ACTIVATION_CODE,    // Code sent when admin activates user
    TENANT_UPLOAD_CODE       // Code sent when tenant is uploaded via file
}

