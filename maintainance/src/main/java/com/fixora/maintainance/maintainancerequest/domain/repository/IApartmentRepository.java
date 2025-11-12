package com.fixora.maintainance.maintainancerequest.domain.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.ApartmentRequest;

import java.util.List;

public interface IApartmentRepository {

    void addApartments(List<ApartmentRequest> apartmentRequests);
}
