package com.fixora.maintainance.maintainancerequest.domain.model;





import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Ticket {

    private Long id;

    private Customer customer;

    private Apartment apartment;


    private Building building;


    private String description;


    private String pictureUrl;


    private LocalDateTime preferredTime;


    private LocalDateTime createdAt ;

    private TicketStatus status ;


    private Maintainer maintainer;


    private Integer customerRate;



}
