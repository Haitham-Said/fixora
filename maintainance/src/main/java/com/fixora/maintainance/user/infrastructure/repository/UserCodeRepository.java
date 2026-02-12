package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.infrastructure.entity.UserCodeEntity;
import com.fixora.maintainance.user.infrastructure.mapper.UserCodeMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserCodeRepository implements IUserCodeRepository {

    private final UserCodeJpaRepository userCodeJpaRepository;

    public UserCodeRepository(UserCodeJpaRepository userCodeJpaRepository) {
        this.userCodeJpaRepository = userCodeJpaRepository;
    }

    @Override
    @Transactional
    public UserCode save(UserCode userCode) {
        UserCodeEntity entity;
        
        if (userCode.getId() != null) {
            // Update existing
            entity = userCodeJpaRepository.findById(userCode.getId())
                    .orElse(new UserCodeEntity());
        } else {
            // Check if user already has a code
            Optional<UserCodeEntity> existingOpt = userCodeJpaRepository.findByUserId(userCode.getUserId());
            entity = existingOpt.orElse(new UserCodeEntity());
        }
        
        entity.setUserId(userCode.getUserId());
        entity.setCode(userCode.getCode());
        entity.setStatus(userCode.getStatus());
        entity.setCreatedAt(userCode.getCreatedAt());
        entity.setExpiresAt(userCode.getExpiresAt());
        entity.setUsedAt(userCode.getUsedAt());
        entity.setIsUsed(userCode.getIsUsed() != null ? userCode.getIsUsed() : false);
        
        UserCodeEntity savedEntity = userCodeJpaRepository.save(entity);
        return UserCodeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserCode> findByUserId(Long userId) {
        return userCodeJpaRepository.findByUserId(userId)
                .map(UserCodeMapper::toDomain);
    }

    @Override
    public Optional<UserCode> findByCode(String code) {
        return userCodeJpaRepository.findByCode(code)
                .map(UserCodeMapper::toDomain);
    }

    @Override
    @Transactional
    public void markCodeAsUsed(String code) {
        userCodeJpaRepository.findByCode(code)
                .ifPresent(entity -> {
                    entity.setIsUsed(true);
                    entity.setUsedAt(LocalDateTime.now());
                    userCodeJpaRepository.save(entity);
                });
    }
}

