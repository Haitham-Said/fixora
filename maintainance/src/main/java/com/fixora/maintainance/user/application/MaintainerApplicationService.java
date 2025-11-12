package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Maintainer;
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

    }
}
