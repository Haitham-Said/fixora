package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketJpaRepository extends JpaRepository<MaintainanceRequest,Long> {

    /** Scheduler queue: assignable tickets without maintainer, retries not exhausted. */
    @Query("""
            SELECT t FROM MaintainanceRequest t
            WHERE t.maintainer IS NULL
              AND t.assignmentRetryCount < 3
              AND t.status = 'READY_TO_ASSIGN'
            """)
    List<MaintainanceRequest> findUnassignedPendingTickets();


}
