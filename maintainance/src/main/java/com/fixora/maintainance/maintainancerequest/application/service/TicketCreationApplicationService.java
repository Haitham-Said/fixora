package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.routing.TicketRoutingService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.CompanyType;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;
import com.fixora.maintainance.property.domain.repository.ICompanyWorkflowConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates tenant ticket creation: resolve PM company, load workflow config, route, persist.
 */
@Service
public class TicketCreationApplicationService {

    private final ICompanyRepository companyRepository;
    private final ICompanyWorkflowConfigRepository companyWorkflowConfigRepository;
    private final TicketRoutingService ticketRoutingService;
    private final ITicketRepository ticketRepository;

    public TicketCreationApplicationService(
            ICompanyRepository companyRepository,
            ICompanyWorkflowConfigRepository companyWorkflowConfigRepository,
            TicketRoutingService ticketRoutingService,
            ITicketRepository ticketRepository) {
        this.companyRepository = companyRepository;
        this.companyWorkflowConfigRepository = companyWorkflowConfigRepository;
        this.ticketRoutingService = ticketRoutingService;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket createTicket(TicketRequest ticketRequest) {
        Company pmCompany = companyRepository.findById(ticketRequest.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + ticketRequest.getCompanyId()));
        if (pmCompany.getType() != CompanyType.PROPERTY_MANAGEMENT) {
            throw new IllegalArgumentException("Tenant tickets must belong to a PROPERTY_MANAGEMENT company");
        }

        CompanyWorkflowConfig config = companyWorkflowConfigRepository.requireByPmCompanyId(pmCompany.getId());
        TicketRoutingDecision routing = ticketRoutingService.route(pmCompany.getId(), config);

        return ticketRepository.createNewTicket(ticketRequest, routing);
    }
}
