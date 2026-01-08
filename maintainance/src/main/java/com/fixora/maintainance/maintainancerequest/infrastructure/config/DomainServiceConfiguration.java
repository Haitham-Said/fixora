package com.fixora.maintainance.maintainancerequest.infrastructure.config;

import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.maintainancerequest.domain.service.DefaultTicketService;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfiguration {

    @Bean
    public TicketService ticketService(ITicketRepository ticketRepository, IUserRepository userRepository) {
        return new DefaultTicketService(ticketRepository, userRepository);
    }
}

