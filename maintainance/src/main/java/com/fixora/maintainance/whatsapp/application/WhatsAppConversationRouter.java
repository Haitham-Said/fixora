package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.domain.model.InboundMessage;
import com.fixora.maintainance.whatsapp.domain.model.OutboundMessage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Routes inbound WhatsApp messages to tenant or maintainer flow.
 * Keeps controller thin and role logic outside transport layer.
 */
@Service
public class WhatsAppConversationRouter {

    private static final String UNKNOWN_USER_MESSAGE = "Your number is not registered for WhatsApp access. Please contact building management.";

    private final ConversationParticipantResolver participantResolver;
    private final ConversationOrchestrator tenantConversationOrchestrator;
    private final MaintainerConversationOrchestrator maintainerConversationOrchestrator;

    public WhatsAppConversationRouter(ConversationParticipantResolver participantResolver,
                                      ConversationOrchestrator tenantConversationOrchestrator,
                                      MaintainerConversationOrchestrator maintainerConversationOrchestrator) {
        this.participantResolver = participantResolver;
        this.tenantConversationOrchestrator = tenantConversationOrchestrator;
        this.maintainerConversationOrchestrator = maintainerConversationOrchestrator;
    }

    public List<OutboundMessage> handle(InboundMessage inbound) {
        var resolution = participantResolver.resolve(inbound);
        return switch (resolution.type()) {
            case TENANT -> tenantConversationOrchestrator.handle(inbound);
            case MAINTAINER -> maintainerConversationOrchestrator.handle(inbound);
            case UNKNOWN -> List.of(new OutboundMessage.Text(inbound.getFromPhone(), UNKNOWN_USER_MESSAGE));
        };
    }
}

