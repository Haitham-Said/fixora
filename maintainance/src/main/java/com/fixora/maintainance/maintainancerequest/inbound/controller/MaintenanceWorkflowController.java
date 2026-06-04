package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.MaintenanceWorkflowApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.inbound.model.workflow.EstimationRequestDto;
import com.fixora.maintainance.maintainancerequest.inbound.model.workflow.MaintenanceWorkflowResponse;
import com.fixora.security.application.model.UserInfo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for MVP maintenance workflow actions (thin: validation + delegation only).
 * Tenant (CUSTOMER) and maintainer have no portal in MVP — excluded from all routes here.
 */
@RestController
@RequestMapping("/api/maintenance-requests/{ticketId}")
public class MaintenanceWorkflowController {

    private final MaintenanceWorkflowApplicationService maintenanceWorkflowApplicationService;

    public MaintenanceWorkflowController(MaintenanceWorkflowApplicationService maintenanceWorkflowApplicationService) {
        this.maintenanceWorkflowApplicationService = maintenanceWorkflowApplicationService;
    }

    @PostMapping("/estimation")
    @PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
    public ResponseEntity<Ticket> submitEstimation(@PathVariable long ticketId,
                                                   @AuthenticationPrincipal UserInfo user,
                                                   @Valid @RequestBody EstimationRequestDto body) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.submitEstimation(
                ticketId, user, body.amount(), body.note()));
    }

    /** Property admin portal; OPERATION may override. */
    @PostMapping("/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN','OPERATION')")
    public ResponseEntity<Ticket> approve(@PathVariable long ticketId,
                                          @AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.approveRequest(ticketId, user));
    }

    @PostMapping("/reject")
    @PreAuthorize("hasAnyAuthority('ADMIN','OPERATION')")
    public ResponseEntity<Ticket> reject(@PathVariable long ticketId,
                                         @AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.rejectRequest(ticketId, user));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
    public ResponseEntity<Ticket> assign(@PathVariable long ticketId,
                                         @AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.assignRequest(ticketId, user));
    }

    @GetMapping("/workflow")
    @PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
    public ResponseEntity<MaintenanceWorkflowResponse> workflow(@PathVariable long ticketId,
                                                                  @AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(maintenanceWorkflowApplicationService.getWorkflowDetails(ticketId, user));
    }
}
