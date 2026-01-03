package com.fixora.maintainance.maintainancerequest.domain.model.requests;

import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class TicketRequest {
    private UUID requestId;
    private Long userId;
    private String userEmail;
    private Long companyId;
    private String description;
    private PreferredSlot preferredSlot;
    private Urgency urgency;
    private String imageUrl;
}