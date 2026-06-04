package com.fixora.maintainance.whatsapp.domain.repository;

import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;

import java.util.List;

public interface ISessionAttachmentRepository {
    void save(Long sessionId, String storageUrl, String mimeType);
    List<ConversationSession.SessionAttachment> findBySessionId(Long sessionId);
    void deleteBySessionId(Long sessionId);
}

