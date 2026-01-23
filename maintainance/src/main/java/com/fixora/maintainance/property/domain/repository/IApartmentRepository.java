package com.fixora.maintainance.property.domain.repository;

import com.fixora.maintainance.property.domain.model.Apartment;
import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;

import java.util.List;

public interface IApartmentRepository {

    void addApartments(List<ApartmentRequest> apartmentRequests);
    Apartment findByBuildingIdAndApartmentNumber(Long buildingId, String apartmentNumber);
}

