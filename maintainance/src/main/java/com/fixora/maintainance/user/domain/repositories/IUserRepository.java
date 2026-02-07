package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.InactiveUser;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    Optional<UserEntity> findUserByUsername(String userName);

    List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id);

     com.fixora.maintainance.user.domain.model.User addUser(MaintainerRequest maintainerRequest);
     com.fixora.maintainance.user.domain.model.User addUser(String name, String email, String phone, String role, Long companyId);
     com.fixora.maintainance.user.domain.model.User addUserWithStatus(String name, String email, String phone, String role, Long companyId, String status);
     
     List<InactiveUser> findInactiveUsers();
     InactiveUser findInactiveUserById(Long userId);
     void activateUser(Long userId);
     Optional<com.fixora.maintainance.user.domain.model.User> findUserById(Long userId);
}
