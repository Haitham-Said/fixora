package com.fixora.maintainance.user.domain.model.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String buildingCode;
    private String apartmentNumber;
    private LocalDate moveInDate;
}

