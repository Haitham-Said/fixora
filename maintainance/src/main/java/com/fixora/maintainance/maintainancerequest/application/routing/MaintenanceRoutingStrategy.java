package com.fixora.maintainance.maintainancerequest.application.routing;

import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;

public interface MaintenanceRoutingStrategy {

    boolean supports(CompanyWorkflowConfig config);

    TicketRoutingDecision route(long pmCompanyId, CompanyWorkflowConfig config);
}
