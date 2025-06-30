package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.User;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepository implements IUserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepository(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findUserByUsername(String userName) {
        return userJpaRepository.findByEmail(userName);
    }
}
