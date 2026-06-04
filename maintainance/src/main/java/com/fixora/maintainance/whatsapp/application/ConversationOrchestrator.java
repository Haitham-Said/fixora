package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import com.fixora.maintainance.whatsapp.domain.model.*;
import com.fixora.maintainance.whatsapp.domain.repository.IConversationSessionRepository;
import com.fixora.maintainance.whatsapp.domain.repository.IInboundMessageRepository;
import com.fixora.maintainance.whatsapp.domain.repository.ISessionAttachmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * DB-backed state machine for WhatsApp conversation flow.
 * Loads/persists session. Produces outbound messages each step.
 */
@Service
public class ConversationOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ConversationOrchestrator.class);
    private static final String PROVIDER = "TWILIO";
    private static final String UNREGISTERED_MESSAGE = "Your number is not registered. Please contact building management to register your phone number.";

    private final IInboundMessageRepository inboundMessageRepository;
    private final IConversationSessionRepository sessionRepository;
    private final ISessionAttachmentRepository attachmentRepository;
    private final TenantResolver tenantResolver;
    private final MediaService mediaService;
    private final TicketCommandService ticketCommandService;
    private final VisitPreferenceClock visitPreferenceClock;
    private final WhatsAppVisitSchedulingPrompts visitSchedulingPrompts;
    private final TwilioWhatsAppContentProperties twilioWhatsAppContent;

    public ConversationOrchestrator(IInboundMessageRepository inboundMessageRepository,
                                   IConversationSessionRepository sessionRepository,
                                   ISessionAttachmentRepository attachmentRepository,
                                   TenantResolver tenantResolver,
                                   MediaService mediaService,
                                   TicketCommandService ticketCommandService,
                                   VisitPreferenceClock visitPreferenceClock,
                                   WhatsAppVisitSchedulingPrompts visitSchedulingPrompts,
                                   TwilioWhatsAppContentProperties twilioWhatsAppContent) {
        this.inboundMessageRepository = inboundMessageRepository;
        this.sessionRepository = sessionRepository;
        this.attachmentRepository = attachmentRepository;
        this.tenantResolver = tenantResolver;
        this.mediaService = mediaService;
        this.ticketCommandService = ticketCommandService;
        this.visitPreferenceClock = visitPreferenceClock;
        this.visitSchedulingPrompts = visitSchedulingPrompts;
        this.twilioWhatsAppContent = twilioWhatsAppContent;
    }

    /**
     * First step: idempotency. If duplicate provider_message_id, return empty (no side effects).
     * handle function is to get the tenant context and load the session or create new if not exist and pass
     * to process input with parameter input and session and tenant
     */
    @Transactional
    public List<OutboundMessage> handle(InboundMessage inbound) {
        boolean isNew = inboundMessageRepository.persistIfNew(
                PROVIDER,
                inbound.getProviderMessageId(),
                inbound.getFromPhone(),
                inbound.getToPhone()
        );
        if (!isNew) {
            log.info("[WHATSAPP_DEBUG] duplicate inbound skipped (idempotency) MessageSid={}", inbound.getProviderMessageId());
            return List.of();
        }

        TenantContext tenant = tenantResolver.resolve(inbound.getFromPhone());
        if (tenant == null) {
            log.warn("[WHATSAPP_DEBUG] unregistered number fromPhone={}", inbound.getFromPhone());
            return List.of(new OutboundMessage.Text(inbound.getFromPhone(), UNREGISTERED_MESSAGE));
        }

        ConversationSession session = sessionRepository.findByFromPhone(inbound.getFromPhone())
                .map(existing -> reconcileSessionWithTenant(existing, tenant))
                .orElseGet(() -> createNewSession(inbound.getFromPhone(), tenant));

        log.info("[WHATSAPP_DEBUG] session loaded/new sessionId={} currentState={} fromPhone={}",
                session.getId(), session.getState(), session.getFromPhone());

        if (inbound.getMedia() != null && !inbound.getMedia().isEmpty()) {
            log.info("[WHATSAPP_DEBUG] persisting {} media item(s) for sessionId={}", inbound.getMedia().size(), session.getId());
            for (InboundMessage.MediaItem m : inbound.getMedia()) {
                String storageUrl = mediaService.downloadAndStore(m.getUrl(), m.getContentType());
                attachmentRepository.save(session.getId(), storageUrl, m.getContentType());
            }
            // Session aggregate is unchanged by attachments (they reference session id); avoid redundant findByFromPhone reload.
        }

        return processInput(session, inbound, tenant);
    }

    private ConversationSession createNewSession(String fromPhone, TenantContext tenant) {
        ConversationSession session = ConversationSession.builder()
                .fromPhone(fromPhone)
                .state(ConversationState.START)
                .companyId(tenant.getCompanyId())
                .tenantId(tenant.getTenantId())
                .apartmentId(tenant.getApartmentId())
                .buildingId(tenant.getBuildingId())
                .lastInteractionAt(Instant.now())
                .build();
        return sessionRepository.save(session);
    }

    /**
     * Tenant may move to another apartment/company while keeping the same phone.
     * Refresh session context and discard any in-progress draft tied to the old property.
     */
    private ConversationSession reconcileSessionWithTenant(ConversationSession session, TenantContext tenant) {
        if (tenantContextMatches(session, tenant)) {
            return session;
        }
        log.info(
                "[WHATSAPP_DEBUG] tenant context changed for fromPhone={} sessionId={} "
                        + "old tenant={}/company={}/apt={} new tenant={}/company={}/apt={} — resetting session",
                session.getFromPhone(),
                session.getId(),
                session.getTenantId(), session.getCompanyId(), session.getApartmentId(),
                tenant.getTenantId(), tenant.getCompanyId(), tenant.getApartmentId()
        );
        if (session.getId() != null) {
            attachmentRepository.deleteBySessionId(session.getId());
        }
        resetSessionForTenant(session, tenant);
        return sessionRepository.save(session);
    }

    private static boolean tenantContextMatches(ConversationSession session, TenantContext tenant) {
        return Objects.equals(session.getTenantId(), tenant.getTenantId())
                && Objects.equals(session.getCompanyId(), tenant.getCompanyId())
                && Objects.equals(session.getApartmentId(), tenant.getApartmentId())
                && Objects.equals(session.getBuildingId(), tenant.getBuildingId());
    }

    private void resetSessionForTenant(ConversationSession session, TenantContext tenant) {
        clearDraftFields(session);
        session.setState(ConversationState.START);
        session.setCompanyId(tenant.getCompanyId());
        session.setTenantId(tenant.getTenantId());
        session.setApartmentId(tenant.getApartmentId());
        session.setBuildingId(tenant.getBuildingId());
        session.setLastInteractionAt(Instant.now());
    }

    private List<OutboundMessage> processInput(ConversationSession session, InboundMessage inbound, TenantContext tenant) {
        String userInput = extractUserInput(inbound);
        ConversationState stateBefore = session.getState();

        List<OutboundMessage> out = switch (stateBefore) {
            case START -> handleStart(session, userInput);
            case CATEGORY -> handleCategory(session, userInput);
            case DESCRIPTION -> handleDescription(session, userInput);
            case MEDIA -> handleMedia(session, userInput, inbound);
            case WAITING_FOR_PREFERRED_DATE -> handlePreferredDate(session, userInput);
            case WAITING_FOR_PREFERRED_TIMESLOT -> handlePreferredTimeSlot(session, userInput);
            case CREATED -> handleCreated(session, userInput);
            case MAINTAINER_MENU,
                 MAINTAINER_VIEWING_OPEN_TICKETS,
                 MAINTAINER_VIEWING_IN_PROGRESS_TICKETS,
                 MAINTAINER_VIEWING_TICKET_ACTION -> unexpectedMaintainerStateInTenantOrchestrator(session, stateBefore);
        };

        ConversationState stateAfter = session.getState();
        log.info("[WHATSAPP_DEBUG] state transition sessionId={} fromPhone={} userInput={} | {} -> {} | outboundCount={}",
                session.getId(), session.getFromPhone(), truncateForLog(userInput, 120),
                stateBefore, stateAfter, out.size());

        return out;
    }

    private List<OutboundMessage> unexpectedMaintainerStateInTenantOrchestrator(
            ConversationSession session,
            ConversationState state
    ) {
        throw new IllegalStateException(
                "Maintainer state " + state + " reached tenant ConversationOrchestrator for sessionId="
                        + session.getId() + " fromPhone=" + session.getFromPhone()
        );
    }

    private static String truncateForLog(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "…";
    }

    private String extractUserInput(InboundMessage inbound) {
        if (inbound.getInteractive() != null) {
            return inbound.getInteractive().getId();
        }
        return inbound.getText() != null ? inbound.getText().trim().toUpperCase(Locale.ROOT) : "";
    }

    private List<OutboundMessage> handleStart(ConversationSession session, String userInput) {
        if ("CREATE_REQUEST".equals(userInput) || "Create Request".equalsIgnoreCase(userInput)) {
            session.setState(ConversationState.CATEGORY);
            touch(session);
            return List.of(buildCategoryList(session.getFromPhone()));
        }
        if ("TRACK_REQUEST".equals(userInput) || "Track Request".equalsIgnoreCase(userInput)) {
            return List.of(new OutboundMessage.Text(session.getFromPhone(),
                    "Track Request is coming soon. For now, use Create Request."));
        }
        return List.of(buildStartButtons(session.getFromPhone()));
    }

    private List<OutboundMessage> handleCategory(ConversationSession session, String userInput) {
        TicketCategory cat = TicketCategory.fromId(userInput);
        if (cat == null) {
            for (TicketCategory c : TicketCategory.values()) {
                if (c.getDisplayName().equalsIgnoreCase(userInput)) {
                    cat = c;
                    break;
                }
            }
        }
        if (cat == null) {
            return List.of(
                    new OutboundMessage.Text(session.getFromPhone(), "Please select a valid category from the list."),
                    buildCategoryList(session.getFromPhone())
            );
        }
        session.setSelectedCategory(cat.getId());
        session.setState(ConversationState.DESCRIPTION);
        touch(session);
        return List.of(new OutboundMessage.Text(session.getFromPhone(),
                "Please type a short description of the issue:"));
    }

    private List<OutboundMessage> handleDescription(ConversationSession session, String userInput) {
        if (userInput == null || userInput.isEmpty()) {
            return List.of(new OutboundMessage.Text(session.getFromPhone(),
                    "Description cannot be empty. Please type a short description:"));
        }
        session.setDescription(userInput);
        session.setState(ConversationState.MEDIA);
        touch(session);
        return List.of(new OutboundMessage.Text(session.getFromPhone(),
                "Send a photo or video of the issue, or type SKIP to continue without media."));
    }

    private List<OutboundMessage> handleMedia(ConversationSession session, String userInput, InboundMessage inbound) {
        if ("SKIP".equals(userInput)) {
            return transitionToDateSelection(session);
        }
        if (inbound.getMedia() != null && !inbound.getMedia().isEmpty()) {
            return transitionToDateSelection(session);
        }
        return List.of(new OutboundMessage.Text(session.getFromPhone(),
                "Please send a photo/video or type SKIP to continue."));
    }

    private List<OutboundMessage> transitionToDateSelection(ConversationSession session) {
        session.setState(ConversationState.WAITING_FOR_PREFERRED_DATE);
        session.setPreferredVisitDate(null);
        session.setPreferredTimeSlot(null);
        touch(session);
        return List.of(visitSchedulingPrompts.preferredDateButtons(session.getFromPhone()));
    }

    private List<OutboundMessage> handlePreferredDate(ConversationSession session, String userInput) {
        PreferredVisitDateOption option = PreferredVisitDateOption.fromId(userInput);
        if (option == null) {
            return List.of(
                    WhatsAppVisitSchedulingPrompts.preferredDateRetry(session.getFromPhone()),
                    visitSchedulingPrompts.preferredDateButtons(session.getFromPhone())
            );
        }
        session.setPreferredVisitDate(option.resolveDate(visitPreferenceClock.getZoneId()));
        session.setState(ConversationState.WAITING_FOR_PREFERRED_TIMESLOT);
        touch(session);
        return List.of(visitSchedulingPrompts.preferredSlotButtons(session.getFromPhone()));
    }

    private List<OutboundMessage> handlePreferredTimeSlot(ConversationSession session, String userInput) {
        PreferredSlot slot = PreferredSlot.fromWhatsAppId(userInput);
        if (slot == null) {
            return List.of(
                    WhatsAppVisitSchedulingPrompts.preferredSlotRetry(session.getFromPhone()),
                    visitSchedulingPrompts.preferredSlotButtons(session.getFromPhone())
            );
        }
        session.setPreferredTimeSlot(slot);
        touch(session);
        return createTicketAndRespond(session);
    }

    private List<OutboundMessage> handleCreated(ConversationSession session, String userInput) {
        if ("CREATE_REQUEST".equals(userInput) || "Create Request".equalsIgnoreCase(userInput)) {
            clearDraftFields(session);
            session.setState(ConversationState.CATEGORY);
            touch(session);
            return List.of(buildCategoryList(session.getFromPhone()));
        }
        return List.of(buildStartButtons(session.getFromPhone()));
    }

    private List<OutboundMessage> createTicketAndRespond(ConversationSession session) {
        if (session.getCreatedTicketId() != null) {
            return List.of(new OutboundMessage.Text(session.getFromPhone(),
                    "Ticket #" + session.getCreatedTicketId() + " was already created for this chat."));
        }

        var result = ticketCommandService.createFromSession(session);
        Long ticketId = result.ticketId();
        log.info("[WHATSAPP_DEBUG] ticket flow sessionId={} ticketId={} alreadyCreated={}",
                session.getId(), ticketId, result.alreadyCreated());
        LocalDate visitDate = session.getPreferredVisitDate();
        PreferredSlot slot = session.getPreferredTimeSlot();
        String categoryId = session.getSelectedCategory();

        session.setCreatedTicketId(ticketId);
        clearSchedulingDraft(session);
        session.setState(ConversationState.CREATED);
        touch(session);

        String body = buildTicketCreatedConfirmation(ticketId, categoryId, visitDate, slot);
        return List.of(new OutboundMessage.Text(session.getFromPhone(), body));
    }

    private String buildTicketCreatedConfirmation(
            Long ticketId,
            String categoryId,
            LocalDate visitDate,
            PreferredSlot slot
    ) {
        TicketCategory cat = TicketCategory.fromId(categoryId);
        String categoryLabel = cat != null ? cat.getDisplayName() : categoryId;
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
        String datePart = visitDate != null ? visitDate.format(df) : "—";
        String slotPart = slot != null ? slot.getDisplayLabel() : "—";
        // Assignment runs after portal workflow (estimate / gates) and the READY_TO_ASSIGN queue — not at WhatsApp create.
        String assignmentLine = "Your building team will process the request; a technician will be assigned when ready - thank you.";
        return String.format(
                """
                Ticket #%d created successfully.
                Category: %s
                Preferred visit date: %s
                Preferred time: %s (preference only)
                %s""",
                ticketId,
                categoryLabel,
                datePart,
                slotPart,
                assignmentLine
        ).trim();
    }

    private void clearDraftFields(ConversationSession session) {
        session.setSelectedCategory(null);
        session.setDescription(null);
        session.setCreatedTicketId(null);
        clearSchedulingDraft(session);
    }

    private void clearSchedulingDraft(ConversationSession session) {
        session.setPreferredVisitDate(null);
        session.setPreferredTimeSlot(null);
    }

    private void touch(ConversationSession session) {
        session.setLastInteractionAt(Instant.now());
        sessionRepository.save(session);
    }

    private OutboundMessage buildStartButtons(String to) {
        List<OutboundMessage.Buttons.Button> buttons = List.of(
                new OutboundMessage.Buttons.Button("CREATE_REQUEST", "Create Request"),
                new OutboundMessage.Buttons.Button("TRACK_REQUEST", "Track Request")
        );
        String sid = twilioWhatsAppContent.getWelcomeButtonsSid();
        String sidOrNull = (sid == null || sid.isBlank()) ? null : sid.trim();
        return new OutboundMessage.Buttons(
                to,
                "Welcome! What would you like to do?",
                buttons,
                sidOrNull,
                twilioWhatsAppContent.getWelcomeButtonsVariables()
        );
    }

    private OutboundMessage buildCategoryList(String to) {
        List<OutboundMessage.ListMessage.Row> rows = new ArrayList<>();
        for (TicketCategory c : TicketCategory.values()) {
            rows.add(new OutboundMessage.ListMessage.Row(c.getId(), c.getDisplayName(), null));
        }
        OutboundMessage.ListMessage.Section section = new OutboundMessage.ListMessage.Section("Categories", rows);
        return new OutboundMessage.ListMessage(to, "Select a category:", List.of(section));
    }
}
