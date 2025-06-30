package com.fixora.maintainance.maintainancerequest.domain.model;


import lombok.Data;

@Data
public class Apartment {

    private Long id;

    private String apartmentNumber;

    private Integer floorNumber;
}
