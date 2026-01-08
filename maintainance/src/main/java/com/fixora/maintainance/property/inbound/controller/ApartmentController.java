package com.fixora.maintainance.property.inbound.controller;

import com.fixora.maintainance.property.application.service.ApartmentApplicationService;
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

