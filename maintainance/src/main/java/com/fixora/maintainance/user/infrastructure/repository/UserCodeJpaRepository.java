package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.UserCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCodeJpaRepository extends JpaRepository<UserCodeEntity, Long> {
    Optional<UserCodeEntity> findByUserId(Long userId);
    Optional<UserCodeEntity> findByCode(String code);
}

