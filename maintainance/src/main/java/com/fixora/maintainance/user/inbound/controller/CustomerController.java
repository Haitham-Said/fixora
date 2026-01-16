package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.CustomerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/customers")
@PreAuthorize("hasAuthority('OPERATION')")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCustomers(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId) {
        customerApplicationService.addCustomers(file, companyId);
        return ResponseEntity.ok("Customers uploaded successfully");
    }
}

