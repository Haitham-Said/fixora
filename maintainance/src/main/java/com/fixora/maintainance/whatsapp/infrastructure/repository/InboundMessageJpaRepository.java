package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.whatsapp.infrastructure.entity.InboundMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InboundMessageJpaRepository extends JpaRepository<InboundMessageEntity, Long> {
    Optional<InboundMessageEntity> findByProviderMessageId(String providerMessageId);
}

