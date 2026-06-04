package com.fixora.maintainance.whatsapp.domain.model;

import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ConversationSession {
    private Long id;
    private String fromPhone;
    private ConversationState state;
    private Long companyId;
    private Long tenantId;
    private Long apartmentId;
    private Long buildingId;
    private String selectedCategory;
    private String description;
    /** Resolved calendar date after tenant picks Today / Tomorrow / Day after tomorrow. */
    private LocalDate preferredVisitDate;
    private PreferredSlot preferredTimeSlot;
    private Long createdTicketId;
    private Instant lastInteractionAt;
    private List<SessionAttachment> attachments;

    @Data
    @Builder
    public static class SessionAttachment {
        private Long id;
        private String storageUrl;
        private String mimeType;
    }
}

