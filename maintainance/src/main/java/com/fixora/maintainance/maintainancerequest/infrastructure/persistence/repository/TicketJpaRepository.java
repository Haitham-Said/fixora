package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketJpaRepository extends JpaRepository<MaintainanceRequest,Long> {

    @Query("SELECT t FROM MaintainanceRequest t WHERE t.status = 'PENDING' AND t.maintainer IS NULL")
    List<MaintainanceRequest> findUnassignedPendingTickets();


}
