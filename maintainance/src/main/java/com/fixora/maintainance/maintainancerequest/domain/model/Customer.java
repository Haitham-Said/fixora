package com.fixora.maintainance.maintainancerequest.domain.model;


import com.fixora.maintainance.user.infrastructure.entity.shared.Apartment;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Customer {

    private User user;

    private LocalDate moveInDate;
}
