package com.fixora.maintainance.property.inbound.controller;

import com.fixora.maintainance.property.application.service.CompanyWorkflowApplicationService;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.inbound.model.CompanyWorkflowConfigDto;
import com.fixora.security.application.model.UserInfo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for company-scoped maintenance workflow flags (MVP).
 * <p>
 * No tenant/maintainer portal: CUSTOMER and MAINTAINER are excluded. Portal may read (ADMIN, FM_ADMIN);
 * OPERATION reads/writes for onboarding. Writes are OPERATION-only (see service guard too).
 */
@RestController
@RequestMapping("/api/companies/{companyId}/workflow-config")
public class CompanyWorkflowController {

    private final CompanyWorkflowApplicationService companyWorkflowApplicationService;

    public CompanyWorkflowController(CompanyWorkflowApplicationService companyWorkflowApplicationService) {
        this.companyWorkflowApplicationService = companyWorkflowApplicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
    public ResponseEntity<CompanyWorkflowConfig> get(@PathVariable long companyId,
                                                     @AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(companyWorkflowApplicationService.get(companyId, user));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('OPERATION')")
    public ResponseEntity<CompanyWorkflowConfig> put(@PathVariable long companyId,
                                                     @AuthenticationPrincipal UserInfo user,
                                                     @Valid @RequestBody CompanyWorkflowConfigDto body) {
        return ResponseEntity.ok(companyWorkflowApplicationService.upsert(companyId, user, body));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OPERATION')")
    public ResponseEntity<CompanyWorkflowConfig> post(@PathVariable long companyId,
                                                      @AuthenticationPrincipal UserInfo user,
                                                      @Valid @RequestBody CompanyWorkflowConfigDto body) {
        return ResponseEntity.ok(companyWorkflowApplicationService.upsert(companyId, user, body));
    }
}
