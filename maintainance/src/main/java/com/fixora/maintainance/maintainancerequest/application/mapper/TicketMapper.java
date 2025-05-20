package com.fixora.maintainance.maintainancerequest.application.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketQuery toDomain(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable) {
        return TicketQuery.builder().
                userId(userInfo.userId())
                .filters(ticketQueryRequest.filters())
                .requestId(ticketQueryRequest.requestId())
                .pageable(pageable)
                .build();
    }
}
