package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.ApartmentApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.service.ApartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/apartments")
@PreAuthorize("hasAuthority('OPERATION')")
public class ApartmentController {

    private final ApartmentApplicationService apartmentService;

    public ApartmentController(ApartmentApplicationService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping
    public void uploadApartments(@RequestParam("file") MultipartFile file){
        apartmentService.addApartments(file);
    }
}
