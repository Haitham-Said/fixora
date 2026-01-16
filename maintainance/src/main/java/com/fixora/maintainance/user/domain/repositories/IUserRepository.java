package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    Optional<UserEntity> findUserByUsername(String userName);


    List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id);

     com.fixora.maintainance.user.domain.model.User addUser(MaintainerRequest maintainerRequest);
     com.fixora.maintainance.user.domain.model.User addUser(com.fixora.maintainance.user.domain.model.request.CustomerRequest customerRequest);




}
