package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;
import com.fixora.maintainance.whatsapp.domain.repository.ISessionAttachmentRepository;
import com.fixora.maintainance.whatsapp.infrastructure.entity.SessionAttachmentEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SessionAttachmentRepository implements ISessionAttachmentRepository {
    private final SessionAttachmentJpaRepository jpaRepository;

    public SessionAttachmentRepository(SessionAttachmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(Long sessionId, String storageUrl, String mimeType) {
        SessionAttachmentEntity entity = new SessionAttachmentEntity();
        entity.setSessionId(sessionId);
        entity.setStorageUrl(storageUrl);
        entity.setMimeType(mimeType);
        entity.setCreatedAt(Instant.now());
        jpaRepository.save(entity);
    }

    @Override
    public List<ConversationSession.SessionAttachment> findBySessionId(Long sessionId) {
        return jpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(a -> ConversationSession.SessionAttachment.builder()
                        .id(a.getId())
                        .storageUrl(a.getStorageUrl())
                        .mimeType(a.getMimeType())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBySessionId(Long sessionId) {
        jpaRepository.deleteBySessionId(sessionId);
    }
}

