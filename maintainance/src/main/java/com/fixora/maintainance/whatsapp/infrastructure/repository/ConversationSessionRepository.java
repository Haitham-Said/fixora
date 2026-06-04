package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;
import com.fixora.maintainance.whatsapp.domain.model.ConversationState;
import com.fixora.maintainance.whatsapp.domain.repository.IConversationSessionRepository;
import com.fixora.maintainance.whatsapp.infrastructure.entity.ConversationSessionEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ConversationSessionRepository implements IConversationSessionRepository {
    private final ConversationSessionJpaRepository jpaRepository;
    private final SessionAttachmentJpaRepository attachmentJpaRepository;

    public ConversationSessionRepository(ConversationSessionJpaRepository jpaRepository,
                                         SessionAttachmentJpaRepository attachmentJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.attachmentJpaRepository = attachmentJpaRepository;
    }

    @Override
    public Optional<ConversationSession> findByFromPhone(String fromPhone) {
        return jpaRepository.findByFromPhone(fromPhone)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public ConversationSession save(ConversationSession session) {
        ConversationSessionEntity entity = toEntity(session);
        ConversationSessionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private ConversationSession toDomain(ConversationSessionEntity entity) {
        List<ConversationSession.SessionAttachment> attachments = attachmentJpaRepository
                .findBySessionIdOrderByCreatedAtAsc(entity.getId())
                .stream()
                .map(a -> ConversationSession.SessionAttachment.builder()
                        .id(a.getId())
                        .storageUrl(a.getStorageUrl())
                        .mimeType(a.getMimeType())
                        .build())
                .collect(Collectors.toList());

        PreferredSlot slot = parsePreferredSlot(entity.getPreferredTimeSlot());

        return ConversationSession.builder()
                .id(entity.getId())
                .fromPhone(entity.getFromPhone())
                .state(ConversationState.valueOf(entity.getState()))
                .companyId(entity.getCompanyId())
                .tenantId(entity.getTenantId())
                .apartmentId(entity.getApartmentId())
                .buildingId(entity.getBuildingId())
                .selectedCategory(entity.getSelectedCategory())
                .description(entity.getDescription())
                .preferredVisitDate(entity.getPreferredVisitDate())
                .preferredTimeSlot(slot)
                .createdTicketId(entity.getCreatedTicketId())
                .lastInteractionAt(entity.getLastInteractionAt())
                .attachments(attachments)
                .build();
    }

    private ConversationSessionEntity toEntity(ConversationSession session) {
        ConversationSessionEntity entity = session.getId() != null
                ? jpaRepository.findById(session.getId()).orElse(new ConversationSessionEntity())
                : new ConversationSessionEntity();
        entity.setFromPhone(session.getFromPhone());
        entity.setState(session.getState().name());
        entity.setCompanyId(session.getCompanyId());
        entity.setTenantId(session.getTenantId());
        entity.setApartmentId(session.getApartmentId());
        entity.setBuildingId(session.getBuildingId());
        entity.setSelectedCategory(session.getSelectedCategory());
        entity.setDescription(session.getDescription());
        entity.setPreferredVisitDate(session.getPreferredVisitDate());
        entity.setPreferredTimeSlot(session.getPreferredTimeSlot() != null ? session.getPreferredTimeSlot().name() : null);
        entity.setCreatedTicketId(session.getCreatedTicketId());
        entity.setLastInteractionAt(session.getLastInteractionAt() != null ? session.getLastInteractionAt() : Instant.now());
        return entity;
    }

    private static PreferredSlot parsePreferredSlot(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        try {
            return PreferredSlot.valueOf(stored.trim());
        } catch (IllegalArgumentException ignored) {
            return PreferredSlot.fromWhatsAppId(stored);
        }
    }
}

