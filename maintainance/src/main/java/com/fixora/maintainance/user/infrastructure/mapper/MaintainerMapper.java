package com.fixora.maintainance.user.infrastructure.mapper;

import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;

public class MaintainerMapper {

    public static com.fixora.maintainance.user.domain.model.Maintainer toDomainMaintainer(MaintainerEntity maintainerEntity){
        return com.fixora.maintainance.user.domain.model.Maintainer.builder()
                .user(UserMapper.toDomain(maintainerEntity.getUser()))
                .rate(maintainerEntity.getRate())
                .profileStatus(maintainerEntity.getProfileStatus())
                .build();

    }


}
