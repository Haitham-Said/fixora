package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.CustomerApplicationService;
import com.fixora.maintainance.user.application.CustomerRegistrationApplicationService;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.inbound.model.CustomerRegistrationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Void> uploadTenants(@RequestParam("file") MultipartFile file){
        customerApplicationService.addTenants(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<Customer> registerCustomer(
            @Valid @ModelAttribute CustomerRegistrationRequestDTO registrationRequest) {
        Customer customer = customerRegistrationApplicationService.registerCustomer(registrationRequest);
        URI location = URI.create("/tenants/" + customer.getUser().getId());
        return ResponseEntity.created(location).body(customer);
    }
}

