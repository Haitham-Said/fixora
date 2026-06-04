package com.fixora.maintainance.maintainancerequest.infrastructure.payment;

import com.fixora.maintainance.maintainancerequest.application.payment.MaintenancePaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fake payment provider for local/dev: deterministic URL + random-looking reference.
 */
@Service
public class StubMaintenancePaymentService implements MaintenancePaymentService {

    @Override
    public PaymentInitResult initiatePayment(long maintenanceRequestId, BigDecimal amount) {
        String ref = "pay_" + maintenanceRequestId + "_" + UUID.randomUUID().toString().substring(0, 8);
        String url = "https://payments.fixora.local/pay?requestId=" + maintenanceRequestId + "&ref=" + ref;
        return new PaymentInitResult(url, ref);
    }
}
