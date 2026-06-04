package com.fixora.maintainance.maintainancerequest.domain.model.requests;

import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
@Builder
public class TicketRequest {
    private UUID requestId;
    private Long userId;
    private String userEmail;
    private Long companyId;
    private String description;
    /** Required: collected in WhatsApp visit-scheduling flow before ticket create. */
    private PreferredSlot preferredSlot;
    /** Required: tenant-chosen visit date from WhatsApp flow. */
    private LocalDate preferredVisitDate;
    private Urgency urgency;
    private String imageUrl;
}