package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.infrastructure.entity.User;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.stereotype.Component;


@Component
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String username) {
         return userRepository.findUserByUsername(username)
                 .orElseThrow(()->new UserNotFoundException("user not exist"));

    }
}
