package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;

import java.util.List;

public interface ApartmentService {

     void addApartments(List<ApartmentRequest> apartmentRequests);
}

