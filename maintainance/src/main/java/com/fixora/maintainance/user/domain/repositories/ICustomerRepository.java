package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.User;

public interface ICustomerRepository {

    Customer addCustomer(User user, Long apartmentId, java.time.LocalDate moveInDate);
}

