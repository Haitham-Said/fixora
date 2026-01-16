package com.fixora.maintainance.user.infrastructure.mapper;

import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;

public class CustomerMapper {

    public static com.fixora.maintainance.user.domain.model.Customer toDomainCustomer(Customer customerEntity){
        com.fixora.maintainance.user.domain.model.Customer customer = new com.fixora.maintainance.user.domain.model.Customer();
        customer.setUser(com.fixora.maintainance.user.infrastructure.mapper.UserMapper.toDomain(customerEntity.getUser()));
        customer.setMoveInDate(customerEntity.getMoveInDate());
        return customer;
    }
}

