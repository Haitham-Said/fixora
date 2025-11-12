package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.ApartmentRequest;

import java.util.List;

public interface ApartmentService {

     void addApartments(List<ApartmentRequest> apartmentRequests);
}
