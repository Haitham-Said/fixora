package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.workflow;

import com.fixora.maintainance.maintainancerequest.domain.model.workflow.WorkflowTicketRow;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;

public final class WorkflowTicketRowMapper {

    private WorkflowTicketRowMapper() {
    }

    public static WorkflowTicketRow toRow(MaintainanceRequest r) {
        long pmCompanyId = r.getPmCompanyId() != null ? r.getPmCompanyId() : 0L;
        long executorCompanyId = r.getExecutorCompanyId() != null ? r.getExecutorCompanyId() : 0L;
        Long fmId = r.getFacilityManagementCompany() != null ? r.getFacilityManagementCompany().getId() : null;
        Long customerUserId = r.getCustomer() != null && r.getCustomer().getUser() != null
                ? r.getCustomer().getUser().getId() : null;
        String phone = r.getCustomer() != null && r.getCustomer().getUser() != null
                ? r.getCustomer().getUser().getPhone() : null;
        return new WorkflowTicketRow(
                r.getId(),
                pmCompanyId,
                executorCompanyId,
                fmId,
                r.getStatus(),
                r.getTicketEstimationActor(),
                r.getEstimatedAmount(),
                r.getEstimationNote(),
                r.isApproved(),
                r.isPaid(),
                r.getPaymentRef(),
                r.getApprovalActor(),
                r.getTicketApprovalStatus(),
                r.getTicketPaymentStatus(),
                customerUserId,
                phone
        );
    }
}
