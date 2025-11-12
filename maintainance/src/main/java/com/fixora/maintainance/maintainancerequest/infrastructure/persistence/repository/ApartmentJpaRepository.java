package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.user.infrastructure.entity.shared.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentJpaRepository extends JpaRepository<Apartment,Long> {
}
