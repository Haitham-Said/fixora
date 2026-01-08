package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentJpaRepository extends JpaRepository<Apartment,Long> {
}

