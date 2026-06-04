package com.fixora.maintainance.maintainancerequest.domain.model;





import com.fixora.maintainance.property.domain.model.Apartment;
import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.Maintainer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Ticket {

    private Long id;

    /** Property management company (tenant portfolio owner). */
    private Long pmCompanyId;
    /** Company whose maintainer pool executes the work. */
    private Long executorCompanyId;
    private Long facilityManagementCompanyId;
    /** @deprecated use {@link #pmCompanyId} */
    private Long companyId;

    private Customer customer;

    private Apartment apartment;


    private Building building;


    private String description;


    private String pictureUrl;


    private String preferredTime;

    private LocalDate preferredVisitDate;

    private LocalDateTime createdAt ;

    private TicketStatus status ;


    private Maintainer maintainer;


    private Integer customerRate;

    // --- MVP workflow fields (mirrored from persistence; optional until populated) ---

    private BigDecimal estimatedAmount;
    private String estimationNote;
    @Builder.Default
    private boolean approved = false;
    @Builder.Default
    private boolean paid = false;
    private String paymentRef;
    /** Last approval actor label stored for audit (e.g. PROPERTY_ADMIN). */
    private String approvalActor;
    @Builder.Default
    private TicketApprovalStatus ticketApprovalStatus = TicketApprovalStatus.NOT_REQUIRED;
    @Builder.Default
    private TicketPaymentStatus ticketPaymentStatus = TicketPaymentStatus.NOT_REQUIRED;
    @Builder.Default
    private TicketPaymentPayerType payerType = TicketPaymentPayerType.NONE;
    private Long approvedBy;
    private LocalDateTime approvedAt;



}
