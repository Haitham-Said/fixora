package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.inbound.model.MaintainerRequestDTO;
import com.fixora.maintainance.user.domain.service.IUserService;
import com.fixora.security.application.model.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class MaintainerApplicationService {
    private final IUserService userService;

    public MaintainerApplicationService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * MVP: maintainers have no portal, but property/FM admins (and OPERATION) create technician users for a company.
     * Company boundary enforced here; not OPERATION-global-only.
     */
    public Maintainer addMaintainer(MaintainerRequestDTO maintainerRequestDTO, UserInfo actor) {
        if (actor == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        Long targetCompanyId = maintainerRequestDTO.getUserDetails().getCompanyId();
        if (!"OPERATION".equalsIgnoreCase(actor.role())) {
            if (actor.companyId() == null || !actor.companyId().equals(targetCompanyId)) {
                throw new IllegalArgumentException("Maintainer must belong to the authenticated company");
            }
        }

        MaintainerRequest maintainerRequest = new MaintainerRequest();
        maintainerRequest.setCompanyId(maintainerRequestDTO.getUserDetails().getCompanyId());
        maintainerRequest.setName(maintainerRequestDTO.getUserDetails().getName());
        maintainerRequest.setEmail(maintainerRequestDTO.getUserDetails().getEmail());
        maintainerRequest.setPhone(maintainerRequestDTO.getUserDetails().getPhone());
        maintainerRequest.setRole(Role.MAINTAINER.name());

        return userService.addMaintainer(maintainerRequest);
    }
}
