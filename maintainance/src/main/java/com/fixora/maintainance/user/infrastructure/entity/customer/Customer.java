package com.fixora.maintainance.user.infrastructure.entity.customer;

import com.fixora.maintainance.user.infrastructure.entity.User;
import com.fixora.maintainance.user.infrastructure.entity.shared.Apartment;
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }
}
