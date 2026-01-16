package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.repositories.ICustomerRepository;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import com.fixora.maintainance.property.domain.service.BuildingService;
import com.fixora.maintainance.property.infrastructure.persistence.repository.ApartmentJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import com.fixora.maintainance.user.infrastructure.mapper.CustomerMapper;

@Repository
public class CustomerRepository implements ICustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final ApartmentJpaRepository apartmentJpaRepository;
    private final BuildingService buildingService;

    public CustomerRepository(CustomerJpaRepository customerJpaRepository, 
                             ApartmentJpaRepository apartmentJpaRepository,
                             BuildingService buildingService) {
        this.customerJpaRepository = customerJpaRepository;
        this.apartmentJpaRepository = apartmentJpaRepository;
        this.buildingService = buildingService;
    }

    @Override
    @Transactional
    public Customer addCustomer(User user, CustomerRequest customerRequest){
        com.fixora.maintainance.user.infrastructure.entity.customer.Customer customerEntity = 
            new com.fixora.maintainance.user.infrastructure.entity.customer.Customer();
        
        // Set user entity - Customer uses @MapsId so userId will be set automatically from userEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        customerEntity.setUserEntity(userEntity);
        customerEntity.setUserId(user.getId());
        
        // Find apartment by building code and apartment number
        com.fixora.maintainance.property.domain.model.Building building = 
            buildingService.getBuildingByBuildingCode(customerRequest.getBuildingCode());
        
        if (building != null) {
            Apartment apartment = apartmentJpaRepository.findByBuildingIdAndApartmentNumber(
                building.getId(), customerRequest.getApartmentNumber());
            
            if (apartment != null) {
                customerEntity.setApartment(apartment);
            }
        }
        
        customerEntity.setMoveInDate(customerRequest.getMoveInDate());
        customerJpaRepository.save(customerEntity);
        return CustomerMapper.toDomainCustomer(customerEntity);
    }
}

