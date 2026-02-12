package com.fixora.maintainance.user.infrastructure.mapper;

import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.infrastructure.entity.UserCodeEntity;

public class UserCodeMapper {
    
    public static UserCode toDomain(UserCodeEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserCode.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .code(entity.getCode())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .usedAt(entity.getUsedAt())
                .isUsed(entity.getIsUsed() != null ? entity.getIsUsed() : false)
                .build();
    }
}

