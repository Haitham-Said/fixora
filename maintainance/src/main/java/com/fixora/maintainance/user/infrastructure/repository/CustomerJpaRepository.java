package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<Customer, UUID> {
}

