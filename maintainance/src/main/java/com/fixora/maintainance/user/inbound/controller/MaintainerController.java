package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.MaintainerApplicationService;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.inbound.model.MaintainerRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/maintainers")
public class MaintainerController {

    private final MaintainerApplicationService maintainerApplicationService;

    public MaintainerController(MaintainerApplicationService maintainerApplicationService) {
        this.maintainerApplicationService = maintainerApplicationService;
    }

    @PostMapping()
    public ResponseEntity<Maintainer> addMaintainer(@RequestBody MaintainerRequestDTO maintainerRequestDTO){

       Maintainer maintainer= maintainerApplicationService.addMaintainer(maintainerRequestDTO);
       return ResponseEntity.created(URI.create("/maintainer/"+maintainer.getUser().getId())).body(maintainer);
    }
}
