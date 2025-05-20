package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.entity.UserEntity;

import java.util.Optional;

public interface IUserService {

    public UserEntity findUserByEmail(String userName);
}
