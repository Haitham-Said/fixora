package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.MaintainerApplicationService;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.inbound.model.MaintainerRequestDTO;
import com.fixora.security.application.model.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/maintainers")
@PreAuthorize("hasAnyAuthority('ADMIN','FM_ADMIN','OPERATION')")
public class MaintainerController {

    private final MaintainerApplicationService maintainerApplicationService;

    public MaintainerController(MaintainerApplicationService maintainerApplicationService) {
        this.maintainerApplicationService = maintainerApplicationService;
    }

    @PostMapping()
    public ResponseEntity<Maintainer> addMaintainer(
            @AuthenticationPrincipal UserInfo user,
            @RequestBody MaintainerRequestDTO maintainerRequestDTO) {

        Maintainer maintainer = maintainerApplicationService.addMaintainer(maintainerRequestDTO, user);
        return ResponseEntity.created(URI.create("/maintainer/" + maintainer.getUser().getId())).body(maintainer);
    }
}
