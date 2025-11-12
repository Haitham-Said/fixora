package com.fixora.maintainance.maintainancerequest.infrastructure.persistence;


import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository.TicketJpaRepository;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.service.UserService;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketAssignmentWorker {

    private final UserService userService;
    private final TicketJpaRepository ticketJpaRepository;

    public TicketAssignmentWorker(UserService userService, TicketJpaRepository ticketJpaRepository) {
        this.userService = userService;
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assignSingleTicketSafely(MaintainanceRequest request) {
        List<Maintainer> maintainers=userService.findAvailableMaintainersForSlotAndCompany(request.getPreferredTime(),request.getCompany().getId());
        if(!maintainers.isEmpty()) {
            MaintainerEntity maintainerEntity = new MaintainerEntity();
            maintainerEntity.setUserId(maintainers.getFirst().getUser().getId());

            request.setMaintainer(maintainerEntity);
            request.setStatus(TicketStatus.ASSIGNED);
            ticketJpaRepository.save(request);
        }
    }
}
