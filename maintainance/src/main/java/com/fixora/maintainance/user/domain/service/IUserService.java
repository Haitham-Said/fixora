package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.infrastructure.entity.User;

public interface IUserService {

    public User findUserByEmail(String userName);
}
