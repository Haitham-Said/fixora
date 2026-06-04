package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import com.fixora.maintainance.whatsapp.domain.model.*;
import com.fixora.maintainance.whatsapp.domain.repository.IConversationSessionRepository;
import com.fixora.maintainance.whatsapp.domain.repository.IInboundMessageRepository;
import com.fixora.maintainance.whatsapp.domain.repository.ISessionAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationOrchestratorSchedulingTest {

    @Mock
    private IInboundMessageRepository inboundMessageRepository;
    @Mock
    private IConversationSessionRepository sessionRepository;
    @Mock
    private ISessionAttachmentRepository attachmentRepository;
    @Mock
    private TenantResolver tenantResolver;
    @Mock
    private MediaService mediaService;
    @Mock
    private TicketCommandService ticketCommandService;
    @Mock
    private VisitPreferenceClock visitPreferenceClock;

    private ConversationOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        lenient().when(visitPreferenceClock.getZoneId()).thenReturn(ZoneId.of("Asia/Dubai"));
        when(inboundMessageRepository.persistIfNew(any(), any(), any(), any())).thenReturn(true);
        TwilioWhatsAppContentProperties contentProps = new TwilioWhatsAppContentProperties();
        WhatsAppVisitSchedulingPrompts visitPrompts = new WhatsAppVisitSchedulingPrompts(contentProps);
        orchestrator = new ConversationOrchestrator(
                inboundMessageRepository,
                sessionRepository,
                attachmentRepository,
                tenantResolver,
                mediaService,
                ticketCommandService,
                visitPreferenceClock,
                visitPrompts,
                contentProps
        );
    }

    @Test
    void mediaSkip_movesToDateSelectionAndSendsDatePrompt() {
        String phone = "+971500000001";
        TenantContext tenant = TenantContext.builder()
                .tenantId(1L).companyId(2L).apartmentId(3L).buildingId(4L).role("CUSTOMER")
                .build();
        when(tenantResolver.resolve(phone)).thenReturn(tenant);

        ConversationSession session = ConversationSession.builder()
                .id(10L)
                .fromPhone(phone)
                .state(ConversationState.MEDIA)
                .tenantId(1L)
                .companyId(2L)
                .apartmentId(3L)
                .buildingId(4L)
                .build();
        when(sessionRepository.findByFromPhone(phone)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InboundMessage inbound = InboundMessage.builder()
                .providerMessageId("SM1")
                .fromPhone(phone)
                .toPhone("whatsapp:+14155238886")
                .type(InboundMessage.MessageType.TEXT)
                .text("SKIP")
                .build();

        List<OutboundMessage> out = orchestrator.handle(inbound);

        ArgumentCaptor<ConversationSession> cap = ArgumentCaptor.forClass(ConversationSession.class);
        verify(sessionRepository, atLeastOnce()).save(cap.capture());
        assertThat(cap.getAllValues())
                .filteredOn(s -> s.getState() == ConversationState.WAITING_FOR_PREFERRED_DATE)
                .isNotEmpty();
        assertThat(out).hasSize(1);
        assertThat(out.getFirst()).isInstanceOf(OutboundMessage.Buttons.class);
        OutboundMessage.Buttons buttons = (OutboundMessage.Buttons) out.getFirst();
        assertThat(buttons.body()).contains("prefer us to visit");
        assertThat(buttons.buttons()).hasSize(3);
    }

    @Test
    void invalidDate_doesNotAdvanceState() {
        String phone = "+971500000002";
        TenantContext tenant = TenantContext.builder()
                .tenantId(1L).companyId(2L).apartmentId(3L).buildingId(4L).role("CUSTOMER")
                .build();
        when(tenantResolver.resolve(phone)).thenReturn(tenant);

        ConversationSession session = ConversationSession.builder()
                .id(11L)
                .fromPhone(phone)
                .state(ConversationState.WAITING_FOR_PREFERRED_DATE)
                .tenantId(1L)
                .companyId(2L)
                .apartmentId(3L)
                .buildingId(4L)
                .build();
        when(sessionRepository.findByFromPhone(phone)).thenReturn(Optional.of(session));

        InboundMessage inbound = InboundMessage.builder()
                .providerMessageId("SM2")
                .fromPhone(phone)
                .toPhone("whatsapp:+14155238886")
                .type(InboundMessage.MessageType.TEXT)
                .text("INVALID")
                .build();

        List<OutboundMessage> out = orchestrator.handle(inbound);

        assertThat(out).hasSizeGreaterThanOrEqualTo(1);
        // Invalid date choice does not touch() the session — no persistence on this path.
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void validSlot_createsTicketAndConfirms() {
        String phone = "+971500000003";
        TenantContext tenant = TenantContext.builder()
                .tenantId(1L).companyId(2L).apartmentId(3L).buildingId(4L).role("CUSTOMER")
                .build();
        when(tenantResolver.resolve(phone)).thenReturn(tenant);

        LocalDate visit = LocalDate.of(2026, 3, 28);
        ConversationSession session = ConversationSession.builder()
                .id(12L)
                .fromPhone(phone)
                .state(ConversationState.WAITING_FOR_PREFERRED_TIMESLOT)
                .tenantId(1L)
                .companyId(2L)
                .apartmentId(3L)
                .buildingId(4L)
                .selectedCategory("PLUMBING")
                .description("Leak under sink")
                .preferredVisitDate(visit)
                .attachments(List.of())
                .build();
        when(sessionRepository.findByFromPhone(phone)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doAnswer(invocation -> {
            ConversationSession s = invocation.getArgument(0);
            assertThat(s.getPreferredTimeSlot()).isEqualTo(PreferredSlot.AFTERNOON);
            assertThat(s.getPreferredVisitDate()).isEqualTo(visit);
            return new TicketCommandService.CreateResult(99L, false);
        }).when(ticketCommandService).createFromSession(any());

        InboundMessage inbound = InboundMessage.builder()
                .providerMessageId("SM3")
                .fromPhone(phone)
                .toPhone("whatsapp:+14155238886")
                .type(InboundMessage.MessageType.INTERACTIVE)
                .interactive(InboundMessage.InteractivePayload.builder()
                        .kind("BUTTON")
                        .id(PreferredSlot.AFTERNOON.getWhatsAppId())
                        .title(PreferredSlot.AFTERNOON.getDisplayLabel())
                        .build())
                .build();

        List<OutboundMessage> out = orchestrator.handle(inbound);

        verify(ticketCommandService).createFromSession(any());
        assertThat(out).hasSize(1);
        assertThat(out.getFirst()).isInstanceOf(OutboundMessage.Text.class);
        assertThat(((OutboundMessage.Text) out.getFirst()).body()).contains("Ticket #99");
        assertThat(((OutboundMessage.Text) out.getFirst()).body()).contains("Plumbing");
        assertThat(((OutboundMessage.Text) out.getFirst()).body()).contains("technician will be assigned when ready");
    }

    @Test
    void staleSession_resetsWhenTenantMovedToAnotherCompany() {
        String phone = "+971500000004";
        TenantContext tenant = TenantContext.builder()
                .tenantId(1L).companyId(99L).apartmentId(30L).buildingId(40L).role("CUSTOMER")
                .build();
        when(tenantResolver.resolve(phone)).thenReturn(tenant);

        ConversationSession session = ConversationSession.builder()
                .id(13L)
                .fromPhone(phone)
                .state(ConversationState.DESCRIPTION)
                .tenantId(1L)
                .companyId(1L)
                .apartmentId(5L)
                .buildingId(6L)
                .selectedCategory("PLUMBING")
                .description("Old apartment leak")
                .build();
        when(sessionRepository.findByFromPhone(phone)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InboundMessage inbound = InboundMessage.builder()
                .providerMessageId("SM4")
                .fromPhone(phone)
                .toPhone("whatsapp:+14155238886")
                .type(InboundMessage.MessageType.TEXT)
                .text("hello")
                .build();

        orchestrator.handle(inbound);

        ArgumentCaptor<ConversationSession> cap = ArgumentCaptor.forClass(ConversationSession.class);
        verify(sessionRepository).save(cap.capture());
        ConversationSession saved = cap.getValue();
        assertThat(saved.getCompanyId()).isEqualTo(99L);
        assertThat(saved.getApartmentId()).isEqualTo(30L);
        assertThat(saved.getBuildingId()).isEqualTo(40L);
        assertThat(saved.getState()).isEqualTo(ConversationState.START);
        assertThat(saved.getSelectedCategory()).isNull();
        assertThat(saved.getDescription()).isNull();
        verify(attachmentRepository).deleteBySessionId(13L);
    }
}
