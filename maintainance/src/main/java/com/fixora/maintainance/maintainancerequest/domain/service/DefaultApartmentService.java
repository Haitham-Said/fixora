package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.IApartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultApartmentService implements ApartmentService{

    private final IApartmentRepository apartmentRepository;

    public DefaultApartmentService(IApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public void addApartments(List<ApartmentRequest> apartmentRequests) {
        apartmentRepository.addApartments(apartmentRequests);
    }
}
