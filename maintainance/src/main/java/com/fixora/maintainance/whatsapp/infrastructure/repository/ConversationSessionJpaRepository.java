package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.whatsapp.infrastructure.entity.ConversationSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationSessionJpaRepository extends JpaRepository<ConversationSessionEntity, Long> {
    Optional<ConversationSessionEntity> findByFromPhone(String fromPhone);
}

