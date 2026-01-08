package com.fixora.maintainance.maintainancerequest.domain.model;





import com.fixora.maintainance.property.domain.model.Apartment;
import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.Maintainer;
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


    private String preferredTime;


    private LocalDateTime createdAt ;

    private TicketStatus status ;


    private Maintainer maintainer;


    private Integer customerRate;




}
