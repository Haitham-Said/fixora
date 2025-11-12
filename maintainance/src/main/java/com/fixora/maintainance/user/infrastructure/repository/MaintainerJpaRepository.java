package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintainerJpaRepository extends JpaRepository<MaintainerEntity, UUID> {
}
