package com.fixora.maintainance.property.inbound.controller;

import com.fixora.maintainance.property.application.service.BuildingApplicationService;
import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.inbound.model.BuildingRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequestMapping("/buildings")
@PreAuthorize("hasAuthority('OPERATION')")
public class BuildingController {

    private final BuildingApplicationService buildingApplicationService;

    public BuildingController(BuildingApplicationService buildingApplicationService) {
        this.buildingApplicationService = buildingApplicationService;
    }

    @PostMapping
    public ResponseEntity<Building> addBuilding(@RequestBody BuildingRequestDTO buildingRequest){
        Building building=buildingApplicationService.addBuilding(buildingRequest);
        URI uri=URI.create("/buildings/"+building.getId());
        return ResponseEntity.created(uri).body(building);
    }
}

