package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.application.notification.MaintenanceWorkflowNotifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Bridges maintenance workflow notifications to Twilio {@link MessageSender} (WhatsApp module).
 */
@Service
public class MaintenanceWorkflowNotifierAdapter implements MaintenanceWorkflowNotifier {

    private final MessageSender messageSender;

    public MaintenanceWorkflowNotifierAdapter(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void notifyApprovalNeeded(String tenantPhone, long ticketId, BigDecimal amount) {
        if (tenantPhone == null || tenantPhone.isBlank()) {
            return;
        }
        String body = "Ticket #" + ticketId + " needs approval. Estimated amount: " + amount + ". Please review in the portal.";
        messageSender.sendTemplate(tenantPhone, body);
    }

    @Override
    public void notifyPaymentLink(String tenantPhone, long ticketId, String paymentUrl, String externalRef) {
        if (tenantPhone == null || tenantPhone.isBlank()) {
            return;
        }
        String body = "Ticket #" + ticketId + " is ready for payment. Ref: " + externalRef + ". Pay here: " + paymentUrl;
        messageSender.sendTemplate(tenantPhone, body);
    }

    @Override
    public void notifyPaymentConfirmed(String tenantPhone, long ticketId) {
        if (tenantPhone == null || tenantPhone.isBlank()) {
            return;
        }
        messageSender.sendTemplate(tenantPhone, "Payment confirmed for ticket #" + ticketId + ". A technician will be assigned soon.");
    }
}
