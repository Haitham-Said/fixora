package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity;


import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;

import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import com.fixora.maintainance.property.infrastructure.entity.Building;
import com.fixora.maintainance.property.infrastructure.entity.Company;
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
    private String preferredTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

//    @ManyToOne
//    @JoinColumn(name = "skillset_id") // tobe added later
//    private Skillset skillset;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "maintainer_id")
    private MaintainerEntity maintainerEntity;

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



//    public Skillset getSkillset() {
//        return skillset;
//    }

    public TicketStatus getStatus() {
        return status;
    }

    public MaintainerEntity getMaintainer() {
        return maintainerEntity;
    }

    public Integer getCustomerRate() {
        return customerRate;
    }

    public Company getCompany() {
        return company;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //    public void setSkillset(Skillset skillset) {
//        this.skillset = skillset;
//    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setMaintainer(MaintainerEntity maintainerEntity) {
        this.maintainerEntity = maintainerEntity;
    }

    public void setCustomerRate(Integer customerRate) {
        this.customerRate = customerRate;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
