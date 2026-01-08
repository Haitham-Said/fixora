package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.property.domain.repository.IApartmentRepository;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;

import com.fixora.maintainance.property.infrastructure.entity.Building;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApartmentRepository implements IApartmentRepository {
    private final ApartmentJpaRepository repository;

    public ApartmentRepository(ApartmentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void addApartments(List<ApartmentRequest> apartmentRequests) {
        List<Apartment> apartmentList=apartmentRequests.stream().map(apartmentRequest -> {
            Apartment apartment=new Apartment();
            Building building=new Building();
            building.setId(apartmentRequest.getBuildingId());
            apartment.setBuilding(building);
            apartment.setApartmentNumber(apartmentRequest.getApartmentNumber());
            apartment.setFloorNumber(apartmentRequest.getFloorNumber());
            return apartment;
        }).toList();

        repository.saveAll(apartmentList);
    }
}

