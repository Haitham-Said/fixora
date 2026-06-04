package com.fixora.maintainance.maintainancerequest.application.notification;

import java.math.BigDecimal;

/**
 * Outbound notifications for workflow steps. Implementation may delegate to WhatsApp / email later.
 */
public interface MaintenanceWorkflowNotifier {

    default void notifyApprovalNeeded(String tenantPhone, long ticketId, BigDecimal amount) {
    }

    default void notifyPaymentLink(String tenantPhone, long ticketId, String paymentUrl, String externalRef) {
    }

    default void notifyPaymentConfirmed(String tenantPhone, long ticketId) {
    }
}
