package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.domain.repositories.IMaintainerRepository;
import com.fixora.maintainance.user.domain.repositories.ICustomerRepository;
import com.fixora.maintainance.property.domain.repository.IApartmentRepository;
import com.fixora.maintainance.property.domain.service.BuildingService;
import org.springframework.stereotype.Component;
import com.fixora.maintainance.user.domain.model.User;

import java.util.List;


@Component
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IMaintainerRepository maintainerRepository;
    private final ICustomerRepository customerRepository;
    private final BuildingService buildingService;
    private final IApartmentRepository apartmentRepository;

    public UserService(IUserRepository userRepository, IMaintainerRepository maintainerRepository, 
                       ICustomerRepository customerRepository, BuildingService buildingService,
                       IApartmentRepository apartmentRepository) {
        this.userRepository = userRepository;
        this.maintainerRepository = maintainerRepository;
        this.customerRepository = customerRepository;
        this.buildingService = buildingService;
        this.apartmentRepository = apartmentRepository;
    }

    public UserEntity findUserByEmail(String username) {
         return userRepository.findUserByUsername(username)
                 .orElseThrow(()->new UserNotFoundException("user not exist"));

    }

    public List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id){
        return userRepository.findAvailableMaintainersForSlotAndCompany(preferredSlot,id);
    }

    public Maintainer addMaintainer(MaintainerRequest maintainerRequest){
        User user = userRepository.addUser(maintainerRequest);
        Maintainer maintainer = maintainerRepository.addMaintainer(user);
        return maintainer;
    }

    public Customer addCustomer(CustomerRequest customerRequest){
        // Business logic: Find building by code
        com.fixora.maintainance.property.domain.model.Building building = buildingService.getBuildingByBuildingCode(customerRequest.getBuildingCode());
        if (building == null) {
            throw new IllegalArgumentException("Building not found with code: " + customerRequest.getBuildingCode());
        }

        // Business logic: Find apartment by building and apartment number
        com.fixora.maintainance.property.domain.model.Apartment apartment = apartmentRepository.findByBuildingIdAndApartmentNumber(
                building.getId(), customerRequest.getApartmentNumber());
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment not found with building code: " + customerRequest.getBuildingCode() 
                    + " and apartment number: " + customerRequest.getApartmentNumber());
        }

        // Create user with CUSTOMER role
        User user = userRepository.addUser(
                customerRequest.getName(),
                customerRequest.getEmail(),
                customerRequest.getPhone() != null ? customerRequest.getPhone() : "",
                Role.CUSTOMER.name(),
                building.getCompanyId()
        );

        // Create customer record
        Customer customer = customerRepository.addCustomer(user, apartment.getId(), customerRequest.getMoveInDate());
        return customer;
    }
}
