package com.fixora.maintainance.property.infrastructure.entity;

import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_workflow_config")
public class CompanyWorkflowConfigEntity {

    @Id
    @Column(name = "pm_company_id")
    private Long pmCompanyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_maintenance_model", length = 40, nullable = false)
    private BusinessMaintenanceModel businessMaintenanceModel = BusinessMaintenanceModel.INTERNAL_MAINTENANCE;

    @Column(name = "facility_management_company_id")
    private Long facilityManagementCompanyId;

    @Column(name = "estimation_required", nullable = false)
    private boolean estimationRequired;

    @Column(name = "approval_required", nullable = false)
    private boolean approvalRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_approval_actor", length = 40)
    private WorkflowApprovalActor workflowApprovalActor = WorkflowApprovalActor.NONE;

    @Column(name = "approval_threshold", precision = 12, scale = 2)
    private BigDecimal approvalThreshold;

    @Column(name = "payment_required", nullable = false)
    private boolean paymentRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_company_payment_mode", length = 40)
    private PropertyCompanyPaymentMode propertyCompanyPaymentMode = PropertyCompanyPaymentMode.NOT_REQUIRED_FOR_MVP;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getPmCompanyId() {
        return pmCompanyId;
    }

    public void setPmCompanyId(Long pmCompanyId) {
        this.pmCompanyId = pmCompanyId;
    }

    public BusinessMaintenanceModel getBusinessMaintenanceModel() {
        return businessMaintenanceModel;
    }

    public void setBusinessMaintenanceModel(BusinessMaintenanceModel businessMaintenanceModel) {
        this.businessMaintenanceModel = businessMaintenanceModel;
    }

    public Long getFacilityManagementCompanyId() {
        return facilityManagementCompanyId;
    }

    public void setFacilityManagementCompanyId(Long facilityManagementCompanyId) {
        this.facilityManagementCompanyId = facilityManagementCompanyId;
    }

    public boolean isEstimationRequired() {
        return estimationRequired;
    }

    public void setEstimationRequired(boolean estimationRequired) {
        this.estimationRequired = estimationRequired;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public WorkflowApprovalActor getWorkflowApprovalActor() {
        return workflowApprovalActor;
    }

    public void setWorkflowApprovalActor(WorkflowApprovalActor workflowApprovalActor) {
        this.workflowApprovalActor = workflowApprovalActor;
    }

    public BigDecimal getApprovalThreshold() {
        return approvalThreshold;
    }

    public void setApprovalThreshold(BigDecimal approvalThreshold) {
        this.approvalThreshold = approvalThreshold;
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public void setPaymentRequired(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }

    public PropertyCompanyPaymentMode getPropertyCompanyPaymentMode() {
        return propertyCompanyPaymentMode;
    }

    public void setPropertyCompanyPaymentMode(PropertyCompanyPaymentMode propertyCompanyPaymentMode) {
        this.propertyCompanyPaymentMode = propertyCompanyPaymentMode;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
