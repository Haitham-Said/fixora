package com.fixora.maintainance.whatsapp.infrastructure.repository;

import com.fixora.maintainance.whatsapp.infrastructure.entity.SessionAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionAttachmentJpaRepository extends JpaRepository<SessionAttachmentEntity, Long> {
    List<SessionAttachmentEntity> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
    void deleteBySessionId(Long sessionId);
}

