package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity;


import com.fixora.maintainance.maintainancerequest.domain.model.TicketApprovalStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentPayerType;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;

import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import com.fixora.maintainance.property.infrastructure.entity.Building;
import com.fixora.maintainance.property.infrastructure.entity.Company;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA aggregate root for a maintenance request ("ticket"). Workflow MVP fields live on this table;
 * company policy lives in {@code company_workflow_config}.
 */
@Entity
@Table(name = "maintenance_requests")
public class MaintainanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(nullable = false)
    private String description;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "preferred_time")
    private String preferredTime;

    @Column(name = "preferred_visit_date")
    private LocalDate preferredVisitDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

//    @ManyToOne
//    @JoinColumn(name = "skillset_id") // tobe added later
//    private Skillset skillset;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.CREATED;

    @Column(name = "estimated_amount", precision = 12, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "estimation_note", columnDefinition = "TEXT")
    private String estimationNote;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "payment_ref")
    private String paymentRef;

    @Column(name = "approval_actor", length = 20)
    private String approvalActor;

    /** Structured approval state (distinct from {@link #approvalActor} label string). */
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_approval_status", length = 40)
    private TicketApprovalStatus ticketApprovalStatus = TicketApprovalStatus.NOT_REQUIRED;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payer_type", length = 40)
    private TicketPaymentPayerType payerType = TicketPaymentPayerType.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 40)
    private TicketPaymentStatus ticketPaymentStatus = TicketPaymentStatus.NOT_REQUIRED;

    @ManyToOne
    @JoinColumn(name = "maintainer_id")
    private MaintainerEntity maintainer;

    @Column(name = "customer_rate")
    private Integer customerRate;

    @Column(name = "assignment_retry_count", nullable = false)
    private Integer assignmentRetryCount = 0;

    @Column(name = "last_assignment_attempt_at")
    private LocalDateTime lastAssignmentAttemptAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pm_company_id", nullable = false)
    private Company pmCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_company_id", nullable = false)
    private Company executorCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_management_company_id")
    private Company facilityManagementCompany;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_estimation_actor", length = 40)
    private WorkflowEstimationActor ticketEstimationActor;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_approval_actor", length = 40)
    private WorkflowApprovalActor ticketApprovalActor;

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public Building getBuilding() {
        return building;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }



//    public Skillset getSkillset() {
//        return skillset;
//    }

    public TicketStatus getStatus() {
        return status;
    }

    public MaintainerEntity getMaintainer() {
        return maintainer;
    }

    public Integer getCustomerRate() {
        return customerRate;
    }

    public Company getPmCompany() {
        return pmCompany;
    }

    public Company getExecutorCompany() {
        return executorCompany;
    }

    public Company getFacilityManagementCompany() {
        return facilityManagementCompany;
    }

    public Long getExecutorCompanyId() {
        return executorCompany != null ? executorCompany.getId() : null;
    }

    public Long getPmCompanyId() {
        return pmCompany != null ? pmCompany.getId() : null;
    }

    public Integer getAssignmentRetryCount() {
        return assignmentRetryCount;
    }

    public LocalDateTime getLastAssignmentAttemptAt() {
        return lastAssignmentAttemptAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public LocalDate getPreferredVisitDate() {
        return preferredVisitDate;
    }

    public void setPreferredVisitDate(LocalDate preferredVisitDate) {
        this.preferredVisitDate = preferredVisitDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //    public void setSkillset(Skillset skillset) {
//        this.skillset = skillset;
//    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setMaintainer(MaintainerEntity maintainer) {
        this.maintainer = maintainer;
    }

    public void setCustomerRate(Integer customerRate) {
        this.customerRate = customerRate;
    }

    public void setPmCompany(Company pmCompany) {
        this.pmCompany = pmCompany;
    }

    public void setExecutorCompany(Company executorCompany) {
        this.executorCompany = executorCompany;
    }

    public void setFacilityManagementCompany(Company facilityManagementCompany) {
        this.facilityManagementCompany = facilityManagementCompany;
    }

    public WorkflowEstimationActor getTicketEstimationActor() {
        return ticketEstimationActor;
    }

    public void setTicketEstimationActor(WorkflowEstimationActor ticketEstimationActor) {
        this.ticketEstimationActor = ticketEstimationActor;
    }

    public WorkflowApprovalActor getTicketApprovalActor() {
        return ticketApprovalActor;
    }

    public void setTicketApprovalActor(WorkflowApprovalActor ticketApprovalActor) {
        this.ticketApprovalActor = ticketApprovalActor;
    }

    public void setAssignmentRetryCount(Integer assignmentRetryCount) {
        this.assignmentRetryCount = assignmentRetryCount;
    }

    public void setLastAssignmentAttemptAt(LocalDateTime lastAssignmentAttemptAt) {
        this.lastAssignmentAttemptAt = lastAssignmentAttemptAt;
    }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public String getEstimationNote() {
        return estimationNote;
    }

    public void setEstimationNote(String estimationNote) {
        this.estimationNote = estimationNote;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getApprovalActor() {
        return approvalActor;
    }

    public void setApprovalActor(String approvalActor) {
        this.approvalActor = approvalActor;
    }

    public TicketApprovalStatus getTicketApprovalStatus() {
        return ticketApprovalStatus;
    }

    public void setTicketApprovalStatus(TicketApprovalStatus ticketApprovalStatus) {
        this.ticketApprovalStatus = ticketApprovalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public TicketPaymentPayerType getPayerType() {
        return payerType;
    }

    public void setPayerType(TicketPaymentPayerType payerType) {
        this.payerType = payerType;
    }

    public TicketPaymentStatus getTicketPaymentStatus() {
        return ticketPaymentStatus;
    }

    public void setTicketPaymentStatus(TicketPaymentStatus ticketPaymentStatus) {
        this.ticketPaymentStatus = ticketPaymentStatus;
    }
}
