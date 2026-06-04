package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.infrastructure.entity.CompanyWorkflowConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for company workflow configuration rows.
 */
@Repository
public interface CompanyWorkflowConfigJpaRepository extends JpaRepository<CompanyWorkflowConfigEntity, Long> {
}
