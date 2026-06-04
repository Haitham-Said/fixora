package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.whatsapp.domain.repository.IInboundMessageRepository;
import com.fixora.maintainance.whatsapp.infrastructure.entity.InboundMessageEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class InboundMessageRepository implements IInboundMessageRepository {
    private final InboundMessageJpaRepository jpaRepository;

    public InboundMessageRepository(InboundMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public boolean persistIfNew(String provider, String providerMessageId, String fromPhone, String toPhone) {
        if (jpaRepository.findByProviderMessageId(providerMessageId).isPresent()) {
            return false;
        }
        InboundMessageEntity entity = new InboundMessageEntity();
        entity.setProvider(provider);
        entity.setProviderMessageId(providerMessageId);
        entity.setFromPhone(fromPhone);
        entity.setToPhone(toPhone);
        entity.setReceivedAt(Instant.now());
        jpaRepository.save(entity);
        return true;
    }
}

