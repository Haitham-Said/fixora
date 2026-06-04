package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.application.service.TicketCreationApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;
import com.fixora.maintainance.whatsapp.domain.model.TicketCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;

@Service
public class TicketCommandService {

    private static final Logger log = LoggerFactory.getLogger(TicketCommandService.class);

    private final TicketCreationApplicationService ticketCreationApplicationService;

    public TicketCommandService(TicketCreationApplicationService ticketCreationApplicationService) {
        this.ticketCreationApplicationService = ticketCreationApplicationService;
    }

    @Transactional
    public CreateResult createFromSession(ConversationSession session) {
        if (session.getCreatedTicketId() != null) {
            log.info("[WHATSAPP_DEBUG] ticket create skipped (session already has ticketId={}) tenantId={}",
                    session.getCreatedTicketId(), session.getTenantId());
            return new CreateResult(session.getCreatedTicketId(), true);
        }
        if (session.getTenantId() == null || session.getCompanyId() == null) {
            throw new IllegalArgumentException("Session missing tenant or company context");
        }
        if (session.getPreferredVisitDate() == null || session.getPreferredTimeSlot() == null) {
            throw new IllegalArgumentException("Session missing preferred visit date or time slot");
        }

        String firstImageUrl = null;
        if (session.getAttachments() != null && !session.getAttachments().isEmpty()) {
            firstImageUrl = session.getAttachments().getFirst().getStorageUrl();
        }

        TicketRequest request = TicketRequest.builder()
                .requestId(UUID.randomUUID())
                .userId(session.getTenantId())
                .userEmail(null)
                .companyId(session.getCompanyId())
                .description(buildDescription(session))
                .preferredSlot(session.getPreferredTimeSlot())
                .preferredVisitDate(session.getPreferredVisitDate())
                .urgency(Urgency.MEDIUM)
                .imageUrl(firstImageUrl)
                .build();

        Ticket ticket = ticketCreationApplicationService.createTicket(request);
        log.info("[WHATSAPP_DEBUG] ticket created ticketId={} status={} pmCompanyId={} executorCompanyId={}",
                ticket.getId(), ticket.getStatus(), ticket.getPmCompanyId(), ticket.getExecutorCompanyId());

        return new CreateResult(ticket.getId(), false);
    }

    private String buildDescription(ConversationSession session) {
        StringBuilder sb = new StringBuilder();
        if (session.getSelectedCategory() != null) {
            TicketCategory cat = TicketCategory.fromId(session.getSelectedCategory());
            sb.append("[").append(cat != null ? cat.getDisplayName() : session.getSelectedCategory()).append("] ");
        }
        sb.append(session.getDescription() != null ? session.getDescription() : "");
        return sb.toString().trim();
    }

    public record CreateResult(Long ticketId, boolean alreadyCreated) {
    }
}
