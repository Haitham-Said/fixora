package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.domain.repositories.ICustomerRepository;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import com.fixora.maintainance.user.infrastructure.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class CustomerRepository implements ICustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerRepository(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    @Transactional
    public Customer addCustomer(User user, Long apartmentId, LocalDate moveInDate) {
        com.fixora.maintainance.user.infrastructure.entity.customer.Customer customerEntity = 
                new com.fixora.maintainance.user.infrastructure.entity.customer.Customer();
        
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        customerEntity.setUser(userEntity);
        
        Apartment apartment = new Apartment();
        apartment.setId(apartmentId);
        customerEntity.setApartment(apartment);
        
        customerEntity.setMoveInDate(moveInDate);
        
        customerJpaRepository.save(customerEntity);
        
        return toDomain(customerEntity);
    }

    private Customer toDomain(com.fixora.maintainance.user.infrastructure.entity.customer.Customer customerEntity) {
        Customer customer = new Customer();
        customer.setUser(UserMapper.toDomain(customerEntity.getUser()));
        customer.setMoveInDate(customerEntity.getMoveInDate());
        return customer;
    }
}
