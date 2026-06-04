package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.CustomerApplicationService;
import com.fixora.maintainance.user.application.CustomerRegistrationApplicationService;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.inbound.model.CustomerRegistrationRequestDTO;
import com.fixora.security.application.model.UserInfo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/tenants")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;
    private final CustomerRegistrationApplicationService customerRegistrationApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService,
                              CustomerRegistrationApplicationService customerRegistrationApplicationService) {
        this.customerApplicationService = customerApplicationService;
        this.customerRegistrationApplicationService = customerRegistrationApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OPERATION')")
    public ResponseEntity<Void> uploadTenants(@RequestParam("file") MultipartFile file) {
        customerApplicationService.addTenants(file);
        return ResponseEntity.ok().build();
    }

    /**
     * MVP: portal-only tenant onboarding (ADMIN / FM_ADMIN / OPERATION). Tenants use WhatsApp only — no activation email/codes.
     */
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
    public ResponseEntity<Customer> registerCustomer(
            @AuthenticationPrincipal UserInfo user,
            @Valid @ModelAttribute CustomerRegistrationRequestDTO registrationRequest) {
        Customer customer = customerRegistrationApplicationService.registerCustomer(registrationRequest, user);
        URI location = URI.create("/tenants/" + customer.getUser().getId());
        return ResponseEntity.created(location).body(customer);
    }
}
