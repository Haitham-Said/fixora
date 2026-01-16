package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;

public interface ICustomerRepository {
    Customer addCustomer(User user, CustomerRequest customerRequest);
}

