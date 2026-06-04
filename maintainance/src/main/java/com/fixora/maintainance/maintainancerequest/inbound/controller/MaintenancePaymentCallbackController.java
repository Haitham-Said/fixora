package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.MaintenanceWorkflowApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.inbound.model.workflow.PaymentCallbackRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public payment callback (MVP). No JWT / role checks — verified via
 * {@link com.fixora.maintainance.maintainancerequest.infrastructure.payment.MaintenancePaymentCallbackValidator}
 * and business rules in the application service (idempotency, amount, status, reference).
 */
@RestController
@RequestMapping("/api/maintenance-payments")
public class MaintenancePaymentCallbackController {

    private final MaintenanceWorkflowApplicationService maintenanceWorkflowApplicationService;

    public MaintenancePaymentCallbackController(MaintenanceWorkflowApplicationService maintenanceWorkflowApplicationService) {
        this.maintenanceWorkflowApplicationService = maintenanceWorkflowApplicationService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Ticket> callback(
            @RequestHeader(value = "X-Fixora-Signature", required = false) String signature,
            @Valid @RequestBody PaymentCallbackRequestDto body) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.markPaidByCallback(signature, body));
    }
}
