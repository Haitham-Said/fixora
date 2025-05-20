package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.entity.UserEntity;

import java.util.Optional;

public interface IUserRepository {

    Optional<UserEntity> findUserByUsername(String userName);
}
