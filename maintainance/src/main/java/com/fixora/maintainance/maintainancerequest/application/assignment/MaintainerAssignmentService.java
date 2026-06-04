package com.fixora.maintainance.maintainancerequest.application.assignment;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository.TicketJpaRepository;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.service.UserService;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Assigns maintainers using {@code executorCompanyId} on the ticket (PM pool or FM pool).
 */
@Service
public class MaintainerAssignmentService {

    private final UserService userService;
    private final TicketJpaRepository ticketJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MaintainerAssignmentService(UserService userService, TicketJpaRepository ticketJpaRepository) {
        this.userService = userService;
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean assignSingleTicketSafely(MaintainanceRequest request) {
        return doAssign(request);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignSingleTicketInCurrentTransaction(MaintainanceRequest request) {
        return doAssign(request);
    }

    private boolean doAssign(MaintainanceRequest request) {
        Long executorCompanyId = request.getExecutorCompanyId();
        if (executorCompanyId == null) {
            return false;
        }
        List<Maintainer> maintainers = userService.findAvailableMaintainersForSlotAndCompany(
                request.getPreferredTime(), executorCompanyId);
        if (maintainers.isEmpty()) {
            return false;
        }
        Long maintainerUserId = maintainers.getFirst().getUser().getId();
        MaintainerEntity maintainerRef = entityManager.getReference(MaintainerEntity.class, maintainerUserId);
        request.setMaintainer(maintainerRef);
        request.setStatus(TicketStatus.ASSIGNED);
        ticketJpaRepository.save(request);
        return true;
    }
}
