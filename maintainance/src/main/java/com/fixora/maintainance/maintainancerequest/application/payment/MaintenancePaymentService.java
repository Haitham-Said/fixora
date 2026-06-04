package com.fixora.maintainance.maintainancerequest.application.payment;

import java.math.BigDecimal;

/**
 * MVP payment integration: generates a fake payment link and reference (no real gateway).
 * Production would swap implementation without changing workflow application service contracts.
 */
public interface MaintenancePaymentService {

    /**
     * @return paymentUrl and reference to show tenant / send over WhatsApp
     */
    PaymentInitResult initiatePayment(long maintenanceRequestId, BigDecimal amount);

    record PaymentInitResult(String paymentUrl, String externalReference) {
    }
}
