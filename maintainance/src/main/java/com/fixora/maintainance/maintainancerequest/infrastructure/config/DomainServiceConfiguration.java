package com.fixora.maintainance.maintainancerequest.infrastructure.config;

import com.fixora.maintainance.maintainancerequest.domain.repository.*;
import com.fixora.maintainance.maintainancerequest.domain.service.*;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfiguration {

    @Bean
    public TicketService ticketService(ITicketRepository ticketRepository, IUserRepository userRepository) {
        return new DefaultTicketService(ticketRepository, userRepository);
    }

    @Bean
    public CompanyService companyService(ICompanyRepository companyRepository) {
        return new DefaultCompanyService(companyRepository);
    }

    @Bean
    public ApartmentService apartmentService(IApartmentRepository apartmentRepository) {
        return new DefaultApartmentService(apartmentRepository);
    }

    @Bean
    public BuildingService buildingService(IBuildingRepository buildingRepository) {
        return new DefaultBuildingRequest(buildingRepository);
    }
}

