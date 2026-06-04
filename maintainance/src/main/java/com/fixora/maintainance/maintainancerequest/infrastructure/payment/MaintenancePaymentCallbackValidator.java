package com.fixora.maintainance.maintainancerequest.infrastructure.payment;

import com.fixora.maintainance.maintainancerequest.domain.exception.MaintenanceWorkflowException;
import com.fixora.maintainance.maintainancerequest.inbound.model.workflow.PaymentCallbackRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Infrastructure: validates payment gateway callbacks (no Spring Security roles).
 * <p>
 * When {@code fixora.payment.callback.secret} is set, {@code X-Fixora-Signature} must be hex(HMAC-SHA256(secret, signingPayload))
 * where signingPayload = {@code ticketId + "|" + amount + "|" + paymentRef + "|" + gatewayStatus}.
 * Replace with provider-specific verification (e.g. Stripe webhook signing) in production.
 */
@Component
public class MaintenancePaymentCallbackValidator {

    private final String secret;

    public MaintenancePaymentCallbackValidator(
            @Value("${fixora.payment.callback.secret:}") String secret) {
        this.secret = secret;
    }

    public boolean isSuccessfulGatewayStatus(String gatewayStatus) {
        return gatewayStatus != null
                && ("SUCCESS".equalsIgnoreCase(gatewayStatus) || "PAID".equalsIgnoreCase(gatewayStatus));
    }

    public void assertValidCallback(String signatureHeader, PaymentCallbackRequestDto body) {
        if (secret == null || secret.isBlank()) {
            return;
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new MaintenanceWorkflowException("Missing X-Fixora-Signature for payment callback");
        }
        String payload = body.ticketId() + "|" + body.amount().toPlainString() + "|" + body.paymentRef() + "|" + body.gatewayStatus();
        String expectedHex = hmacHex(secret, payload);
        String got = signatureHeader.trim();
        if (!constantTimeEquals(expectedHex, got)) {
            throw new MaintenanceWorkflowException("Invalid payment callback signature");
        }
    }

    private static String hmacHex(String secretKey, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new MaintenanceWorkflowException("Could not verify payment callback signature");
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        if (x.length != y.length) {
            return false;
        }
        int d = 0;
        for (int i = 0; i < x.length; i++) {
            d |= x[i] ^ y[i];
        }
        return d == 0;
    }
}
