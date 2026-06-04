package com.fixora.maintainance.whatsapp.domain.repository;

import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;

import java.util.Optional;

public interface IConversationSessionRepository {
    Optional<ConversationSession> findByFromPhone(String fromPhone);
    ConversationSession save(ConversationSession session);
}

