package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity;


import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.Maintainer;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.Skillset;
import com.fixora.maintainance.user.infrastructure.entity.shared.Apartment;
import com.fixora.maintainance.user.infrastructure.entity.shared.Building;
import com.fixora.maintainance.user.infrastructure.entity.shared.Company;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_requests")
public class MaintainanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(nullable = false)
    private String description;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "preferred_time")
    private LocalDateTime preferredTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "skillset_id")
    private Skillset skillset;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "maintainer_id")
    private Maintainer maintainer;

    @Column(name = "customer_rate")
    private Integer customerRate;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public Building getBuilding() {
        return building;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public LocalDateTime getPreferredTime() {
        return preferredTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Skillset getSkillset() {
        return skillset;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Maintainer getMaintainer() {
        return maintainer;
    }

    public Integer getCustomerRate() {
        return customerRate;
    }

    public Company getCompany() {
        return company;
    }
}
