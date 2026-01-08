package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.infrastructure.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJPARepository extends JpaRepository<Company,Long> {
}

