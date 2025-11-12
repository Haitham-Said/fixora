package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.stereotype.Component;
import com.fixora.maintainance.user.domain.model.User;

import java.util.List;


@Component
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findUserByEmail(String username) {
         return userRepository.findUserByUsername(username)
                 .orElseThrow(()->new UserNotFoundException("user not exist"));

    }

    public List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id){
        return userRepository.findAvailableMaintainersForSlotAndCompany(preferredSlot,id);
    }

    public Maintainer addMaintainer(MaintainerRequest maintainerRequest){
        User user= userRepository.addUser(maintainerRequest);



    }
}
