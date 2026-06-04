package com.fixora.maintainance.maintainancerequest.inbound.model.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Unauthenticated callback payload (MVP). Secured by {@link com.fixora.maintainance.maintainancerequest.infrastructure.payment.MaintenancePaymentCallbackValidator}
 * when {@code fixora.payment.callback.secret} is configured — not JWT roles.
 */
public record PaymentCallbackRequestDto(
        @NotNull Long ticketId,
        @NotNull BigDecimal amount,
        @NotBlank String paymentRef,
        /** Gateway-reported outcome, e.g. SUCCESS / PAID / FAILED. */
        @NotBlank String gatewayStatus
) {
}
