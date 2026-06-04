package com.fixora.maintainance.maintainancerequest.application.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.PaginationRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketFilter;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper.PaginationMapper;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketQuery toDomain(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable) {
        return TicketQuery.builder()
                .userId(userInfo.userId())
                .filter(TicketFilter.builder()
                        .companyId(ticketQueryRequest.companyId())
                        .ticketStatus(ticketQueryRequest.ticketStatus())
                        .dateFrom(ticketQueryRequest.dateFrom())
                        .dateTo(ticketQueryRequest.dateTo())
                        .role(ticketQueryRequest.role())
                        .queue(ticketQueryRequest.queue())
                        .build())
                .requestId(ticketQueryRequest.requestId())
                .pagination(PaginationMapper.toDomain(pageable))
                .build();
    }
}
