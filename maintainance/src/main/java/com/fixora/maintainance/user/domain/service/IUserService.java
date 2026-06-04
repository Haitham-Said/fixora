package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.model.request.CustomerRegistrationRequest;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;

public interface IUserService {

     UserEntity findUserByEmail(String userName);
     Maintainer addMaintainer(MaintainerRequest maintainerRequest);
     Customer addCustomer(CustomerRequest customerRequest);
     Customer registerCustomer(CustomerRegistrationRequest registrationRequest);
}
