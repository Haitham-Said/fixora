package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.inbound.model.MaintainerRequestDTO;
import com.fixora.maintainance.user.domain.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class MaintainerApplicationService {
    private final IUserService userService;

    public MaintainerApplicationService(IUserService userService) {
        this.userService = userService;
    }

    public Maintainer addMaintainer(MaintainerRequestDTO maintainerRequestDTO){
        // Convert DTO to domain request
        MaintainerRequest maintainerRequest = new MaintainerRequest();
        maintainerRequest.setCompanyId(maintainerRequestDTO.getUserDetails().getCompanyId());
        maintainerRequest.setName(maintainerRequestDTO.getUserDetails().getName());
        maintainerRequest.setEmail(maintainerRequestDTO.getUserDetails().getEmail());
        maintainerRequest.setPhone(maintainerRequestDTO.getUserDetails().getPhone());
        maintainerRequest.setRole(Role.MAINTAINER.name());

        // Delegate to domain service to create user and maintainer
        return userService.addMaintainer(maintainerRequest);
    }
}
