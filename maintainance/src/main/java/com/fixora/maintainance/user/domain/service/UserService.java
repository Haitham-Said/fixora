package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.domain.repositories.IMaintainerRepository;
import com.fixora.maintainance.user.domain.repositories.ICustomerRepository;
import org.springframework.stereotype.Component;
import com.fixora.maintainance.user.domain.model.User;

import java.util.List;


@Component
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IMaintainerRepository maintainerRepository;
    private final ICustomerRepository customerRepository;

    public UserService(IUserRepository userRepository, IMaintainerRepository maintainerRepository, ICustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.maintainerRepository = maintainerRepository;
        this.customerRepository = customerRepository;
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

    public Customer addCustomer(com.fixora.maintainance.user.domain.model.request.CustomerRequest customerRequest){
        User user = userRepository.addUser(customerRequest);
        Customer customer = customerRepository.addCustomer(user, customerRequest);
        return customer;
    }
}
