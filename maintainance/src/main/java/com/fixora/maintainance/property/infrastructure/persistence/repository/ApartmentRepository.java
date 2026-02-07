package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.domain.model.Apartment;
import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.property.domain.repository.IApartmentRepository;
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
        List<com.fixora.maintainance.property.infrastructure.entity.Apartment> apartmentList=apartmentRequests.stream().map(apartmentRequest -> {
            com.fixora.maintainance.property.infrastructure.entity.Apartment apartment=new com.fixora.maintainance.property.infrastructure.entity.Apartment();
            Building building=new Building();
            building.setId(apartmentRequest.getBuildingId());
            apartment.setBuilding(building);
            apartment.setApartmentNumber(apartmentRequest.getApartmentNumber());
            apartment.setFloorNumber(apartmentRequest.getFloorNumber());
            return apartment;
        }).toList();

        repository.saveAll(apartmentList);
    }

    @Override
    public Apartment findByBuildingIdAndApartmentNumber(Long buildingId, String apartmentNumber) {
        com.fixora.maintainance.property.infrastructure.entity.Apartment apartmentEntity = repository.findByBuildingIdAndApartmentNumber(buildingId, apartmentNumber);
        if (apartmentEntity == null) {
            return null;
        }
        return toDomain(apartmentEntity);
    }

    @Override
    public Apartment findById(Long apartmentId) {
        return repository.findById(apartmentId)
                .map(this::toDomain)
                .orElse(null);
    }

    private Apartment toDomain(com.fixora.maintainance.property.infrastructure.entity.Apartment apartmentEntity) {
        Apartment apartment = new Apartment();
        apartment.setId(apartmentEntity.getId());
        apartment.setApartmentNumber(apartmentEntity.getApartmentNumber());
        apartment.setFloorNumber(apartmentEntity.getFloorNumber());
        apartment.setBuildingId(apartmentEntity.getBuilding() != null ? apartmentEntity.getBuilding().getId() : null);
        return apartment;
    }
}

