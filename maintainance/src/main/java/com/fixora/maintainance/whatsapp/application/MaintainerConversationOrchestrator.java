package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.whatsapp.domain.model.ConversationSession;
import com.fixora.maintainance.whatsapp.domain.model.ConversationState;
import com.fixora.maintainance.whatsapp.domain.model.InboundMessage;
import com.fixora.maintainance.whatsapp.domain.model.MaintainerContext;
import com.fixora.maintainance.whatsapp.domain.model.OutboundMessage;
import com.fixora.maintainance.whatsapp.domain.repository.IConversationSessionRepository;
import com.fixora.maintainance.whatsapp.domain.repository.IInboundMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MaintainerConversationOrchestrator {

    private static final String PROVIDER = "TWILIO";

    private final IInboundMessageRepository inboundMessageRepository;
    private final IConversationSessionRepository sessionRepository;
    private final MaintainerResolver maintainerResolver;
    private final TicketService ticketService;
    private final MaintainerActionIdParser actionIdParser;

    public MaintainerConversationOrchestrator(IInboundMessageRepository inboundMessageRepository,
                                              IConversationSessionRepository sessionRepository,
                                              MaintainerResolver maintainerResolver,
                                              TicketService ticketService,
                                              MaintainerActionIdParser actionIdParser) {
        this.inboundMessageRepository = inboundMessageRepository;
        this.sessionRepository = sessionRepository;
        this.maintainerResolver = maintainerResolver;
        this.ticketService = ticketService;
        this.actionIdParser = actionIdParser;
    }

    @Transactional
    public List<OutboundMessage> handle(InboundMessage inbound) {
        boolean isNew = inboundMessageRepository.persistIfNew(
                PROVIDER,
                inbound.getProviderMessageId(),
                inbound.getFromPhone(),
                inbound.getToPhone()
        );
        if (!isNew) return List.of();

        MaintainerContext maintainer = maintainerResolver.resolve(inbound.getFromPhone());
        if (maintainer == null) {
            return List.of(new OutboundMessage.Text(inbound.getFromPhone(),
                    "Your number is not registered as a maintainer. Please contact admin."));
        }

        ConversationSession session = sessionRepository.findByFromPhone(inbound.getFromPhone())
                .orElseGet(() -> createMaintainerSession(inbound.getFromPhone(), maintainer));

        String userInput = extractUserInput(inbound);
        MaintainerActionIdParser.ParsedAction action = actionIdParser.parse(userInput);

        return switch (action.kind()) {
            case MENU_OPEN -> showOpenTickets(session, maintainer);
            case MENU_IN_PROGRESS -> showInProgressTickets(session, maintainer);
            case TICKET_SELECTED -> showTicketActionForCurrentView(session, maintainer, action.ticketId());
            case TICKET_START -> startTicket(session, maintainer, action.ticketId());
            case TICKET_COMPLETE -> completeTicket(session, maintainer, action.ticketId());
            case INVALID -> showMenu(session);
        };
    }

    private List<OutboundMessage> showTicketActionForCurrentView(ConversationSession session, MaintainerContext maintainer, Long ticketId) {
        if (ticketId == null) {
            return invalidAction(session);
        }
        if (session.getState() == ConversationState.MAINTAINER_VIEWING_OPEN_TICKETS) {
            return showTicketAction(session, maintainer, ticketId, true);
        }
        if (session.getState() == ConversationState.MAINTAINER_VIEWING_IN_PROGRESS_TICKETS) {
            return showTicketAction(session, maintainer, ticketId, false);
        }
        List<OutboundMessage> out = new ArrayList<>();
        out.add(new OutboundMessage.Text(
                session.getFromPhone(),
                "Please choose Open or In Progress list first, then send the ticket number."
        ));
        out.addAll(showMenu(session));
        return out;
    }

    private ConversationSession createMaintainerSession(String fromPhone, MaintainerContext maintainer) {
        ConversationSession session = ConversationSession.builder()
                .fromPhone(fromPhone)
                .state(ConversationState.MAINTAINER_MENU)
                .companyId(maintainer.getCompanyId())
                .tenantId(maintainer.getMaintainerUserId())
                .lastInteractionAt(Instant.now())
                .build();
        return sessionRepository.save(session);
    }

    private String extractUserInput(InboundMessage inbound) {
        if (inbound.getInteractive() != null && inbound.getInteractive().getId() != null) {
            return inbound.getInteractive().getId();
        }
        return inbound.getText() != null ? inbound.getText().trim() : "";
    }

    private List<OutboundMessage> showMenu(ConversationSession session) {
        session.setState(ConversationState.MAINTAINER_MENU);
        touch(session);
        List<OutboundMessage.Buttons.Button> buttons = List.of(
                new OutboundMessage.Buttons.Button("open", "My Open Tickets"),
                new OutboundMessage.Buttons.Button("inprogress", "My In Progress Tickets")
        );
        return List.of(new OutboundMessage.Buttons(
                session.getFromPhone(),
                "Maintainer menu. Choose an option:",
                buttons
        ));
    }

    private List<OutboundMessage> showOpenTickets(ConversationSession session, MaintainerContext maintainer) {
        List<Ticket> tickets = ticketService.findMaintainerTicketsByStatus(maintainer.getMaintainerUserId(), TicketStatus.ASSIGNED);
        session.setState(ConversationState.MAINTAINER_VIEWING_OPEN_TICKETS);
        touch(session);
        if (tickets.isEmpty()) {
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(), "You have no open tickets right now."));
            out.addAll(showMenu(session));
            return out;
        }
        return List.of(buildTicketListMessage(session.getFromPhone(), "My Open Tickets", tickets, true));
    }

    private List<OutboundMessage> showInProgressTickets(ConversationSession session, MaintainerContext maintainer) {
        List<Ticket> tickets = ticketService.findMaintainerTicketsByStatus(maintainer.getMaintainerUserId(), TicketStatus.IN_PROGRESS);
        session.setState(ConversationState.MAINTAINER_VIEWING_IN_PROGRESS_TICKETS);
        touch(session);
        if (tickets.isEmpty()) {
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(), "You have no in-progress tickets right now."));
            out.addAll(showMenu(session));
            return out;
        }
        return List.of(buildTicketListMessage(session.getFromPhone(), "My In Progress Tickets", tickets, false));
    }

    private List<OutboundMessage> showTicketAction(ConversationSession session, MaintainerContext maintainer, Long ticketId, boolean fromOpenList) {
        if (ticketId == null) {
            return invalidAction(session);
        }
        Optional<Ticket> ticketOpt = ticketService.findMaintainerTicketById(maintainer.getMaintainerUserId(), ticketId);
        if (ticketOpt.isEmpty()) {
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(), "That ticket is no longer available for your account."));
            out.addAll(showMenu(session));
            return out;
        }
        Ticket ticket = ticketOpt.get();
        String body = buildTicketDetails(ticket);
        String actionId = fromOpenList
                ? "start:" + ticket.getId()
                : "complete:" + ticket.getId();
        String actionTitle = fromOpenList ? "Start" : "Complete";
        session.setState(ConversationState.MAINTAINER_VIEWING_TICKET_ACTION);
        touch(session);
        return List.of(
                new OutboundMessage.Text(session.getFromPhone(), body),
                new OutboundMessage.Buttons(
                        session.getFromPhone(),
                        "Next action:",
                        List.of(new OutboundMessage.Buttons.Button(actionId, actionTitle))
                )
        );
    }

    private List<OutboundMessage> startTicket(ConversationSession session, MaintainerContext maintainer, Long ticketId) {
        if (ticketId == null) return invalidAction(session);
        try {
            ticketService.startTicketForMaintainer(ticketId, maintainer.getMaintainerUserId());
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(),
                    "Ticket #" + ticketId + " is now IN_PROGRESS."));
            out.addAll(showMenu(session));
            return out;
        } catch (IllegalArgumentException ex) {
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(), safeMessage(ex.getMessage())));
            out.addAll(showMenu(session));
            return out;
        }
    }

    private List<OutboundMessage> completeTicket(ConversationSession session, MaintainerContext maintainer, Long ticketId) {
        if (ticketId == null) return invalidAction(session);
        try {
            ticketService.completeTicketForMaintainer(ticketId, maintainer.getMaintainerUserId());
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(),
                    "Ticket #" + ticketId + " is marked COMPLETED. Great work."));
            out.addAll(showMenu(session));
            return out;
        } catch (IllegalArgumentException ex) {
            List<OutboundMessage> out = new ArrayList<>();
            out.add(new OutboundMessage.Text(session.getFromPhone(), safeMessage(ex.getMessage())));
            out.addAll(showMenu(session));
            return out;
        }
    }

    private List<OutboundMessage> invalidAction(ConversationSession session) {
        List<OutboundMessage> out = new ArrayList<>();
        out.add(new OutboundMessage.Text(session.getFromPhone(), "Sorry, I couldn't understand that action."));
        out.addAll(showMenu(session));
        return out;
    }

    private OutboundMessage buildTicketListMessage(String to, String title, List<Ticket> tickets, boolean openList) {
        List<OutboundMessage.ListMessage.Row> rows = new ArrayList<>();
        for (Ticket t : tickets) {
            String id = String.valueOf(t.getId());
            String label = "#" + t.getId() + " - " + compactCategory(t) + " - Apt " + apartmentLabel(t);
            rows.add(new OutboundMessage.ListMessage.Row(id, truncate(label, 70), null));
        }
        OutboundMessage.ListMessage.Section section = new OutboundMessage.ListMessage.Section(title, rows);
        return new OutboundMessage.ListMessage(
                to,
                "Select a ticket or type the ticket number (e.g. 20):",
                List.of(section)
        );
    }

    private String buildTicketDetails(Ticket ticket) {
        return ("Ticket #" + ticket.getId() + "\n"
                + "Status: " + ticket.getStatus() + "\n"
                + "Apartment: " + apartmentLabel(ticket) + "\n"
                + "Description: " + (ticket.getDescription() == null ? "-" : ticket.getDescription())).trim();
    }

    private String compactCategory(Ticket ticket) {
        String desc = ticket.getDescription();
        if (desc == null || desc.isBlank()) return "Issue";
        String d = desc.trim();
        if (d.startsWith("[") && d.contains("]")) {
            return d.substring(1, d.indexOf("]")).trim();
        }
        return d.substring(0, Math.min(d.length(), 18));
    }

    private String apartmentLabel(Ticket ticket) {
        if (ticket.getApartment() == null || ticket.getApartment().getApartmentNumber() == null) {
            return "-";
        }
        return ticket.getApartment().getApartmentNumber().toUpperCase(Locale.ROOT);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }

    private String safeMessage(String domainMsg) {
        if (domainMsg == null || domainMsg.isBlank()) {
            return "Action cannot be completed because ticket state changed.";
        }
        return domainMsg;
    }

    private void touch(ConversationSession session) {
        session.setLastInteractionAt(Instant.now());
        sessionRepository.save(session);
    }
}

