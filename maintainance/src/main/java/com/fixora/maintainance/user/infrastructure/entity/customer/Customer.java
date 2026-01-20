package com.fixora.maintainance.user.infrastructure.entity.customer;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    public Long getUserId() {
        return userId;
    }

    public UserEntity getUser() {
        return userEntity;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }
}
