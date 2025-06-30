package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.infrastructure.entity.User;

import java.util.Optional;

public interface IUserRepository {

    Optional<User> findUserByUsername(String userName);
}
