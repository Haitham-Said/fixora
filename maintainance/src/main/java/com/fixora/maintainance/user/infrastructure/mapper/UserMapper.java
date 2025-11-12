package com.fixora.maintainance.user.infrastructure.mapper;

import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;

public class UserMapper {

    public static  User toDomain(UserEntity userEntity){
        User user=new User();
        user.setName(userEntity.getName());
        user.setId(userEntity.getId());
        user.setEmail(userEntity.getEmail());
        user.setPhone(userEntity.getPhone());
        return user;

    }
}
