package com.fixora.maintainance.maintainancerequest.application.routing;

import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketRoutingService {

    private final List<MaintenanceRoutingStrategy> strategies;

    public TicketRoutingService(List<MaintenanceRoutingStrategy> strategies) {
        this.strategies = strategies;
    }

    public TicketRoutingDecision route(long pmCompanyId, CompanyWorkflowConfig config) {
        return strategies.stream()
                .filter(s -> s.supports(config))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No routing strategy for businessMaintenanceModel: " + config.getBusinessMaintenanceModel()))
                .route(pmCompanyId, config);
    }
}
