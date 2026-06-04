package com.fixora.maintainance.whatsapp.domain.repository;

/**
 * Returns true if message was newly persisted, false if duplicate (idempotency).
 */
public interface IInboundMessageRepository {
    boolean persistIfNew(String provider, String providerMessageId, String fromPhone, String toPhone);
}

