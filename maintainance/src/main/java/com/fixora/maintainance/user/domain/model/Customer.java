package com.fixora.maintainance.user.domain.model;


import lombok.Data;

import java.time.LocalDate;

@Data
public class Customer {

    private User user;

    private LocalDate moveInDate;
}
