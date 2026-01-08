package com.fixora.maintainance.property.infrastructure.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "apartments", uniqueConstraints = @UniqueConstraint(columnNames = {"building_id", "apartment_number"}))
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    public Long getId() {
        return id;
    }

    public Building getBuilding() {
        return building;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }
}

