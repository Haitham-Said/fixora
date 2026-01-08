package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.infrastructure.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingJPARepository extends JpaRepository<Building,Long> {
    Building findByBuildingCode(String buildingCode);
}

