package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;

public interface IUserService {

     UserEntity findUserByEmail(String userName);
     Maintainer addMaintainer(MaintainerRequest maintainerRequest);
}
