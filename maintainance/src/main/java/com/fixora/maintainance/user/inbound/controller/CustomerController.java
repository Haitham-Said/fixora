package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.CustomerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tenants")
@PreAuthorize("hasAuthority('OPERATION')")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @PostMapping
    public ResponseEntity<Void> uploadTenants(@RequestParam("file") MultipartFile file){
        customerApplicationService.addTenants(file);
        return ResponseEntity.ok().build();
    }
}

