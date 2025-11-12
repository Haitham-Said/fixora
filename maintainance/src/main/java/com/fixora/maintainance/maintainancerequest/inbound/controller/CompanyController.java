package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.CompanyApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.inbound.model.CompanyRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/companies")
@PreAuthorize("hasAuthority('OPERATION')")
public class CompanyController {

    private final CompanyApplicationService companyApplicationService;

    public CompanyController(CompanyApplicationService companyApplicationService) {
        this.companyApplicationService = companyApplicationService;
    }

    @PostMapping
    public ResponseEntity<Company> addCompany(@RequestBody CompanyRequestDTO companyRequestDTO){
        Company company=companyApplicationService.addCompany(companyRequestDTO);

        URI location=URI.create("/companies/"+company.getId());
        return ResponseEntity.created(location).body(company);
    }
}
