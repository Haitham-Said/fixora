package com.fixora.maintainance.whatsapp.infrastructure.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "conversation_session", indexes = @Index(unique = true, columnList = "from_phone"))
public class ConversationSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_phone", nullable = false, unique = true)
    private String fromPhone;

    @Column(nullable = false)
    private String state;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "selected_category")
    private String selectedCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_ticket_id")
    private Long createdTicketId;

    @Column(name = "preferred_visit_date")
    private LocalDate preferredVisitDate;

    /** PreferredSlot.name() e.g. MORNING */
    @Column(name = "preferred_time_slot")
    private String preferredTimeSlot;

    @Column(name = "last_interaction_at", nullable = false)
    private Instant lastInteractionAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedTicketId() {
        return createdTicketId;
    }

    public void setCreatedTicketId(Long createdTicketId) {
        this.createdTicketId = createdTicketId;
    }

    public LocalDate getPreferredVisitDate() {
        return preferredVisitDate;
    }

    public void setPreferredVisitDate(LocalDate preferredVisitDate) {
        this.preferredVisitDate = preferredVisitDate;
    }

    public String getPreferredTimeSlot() {
        return preferredTimeSlot;
    }

    public void setPreferredTimeSlot(String preferredTimeSlot) {
        this.preferredTimeSlot = preferredTimeSlot;
    }

    public Instant getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }
}

