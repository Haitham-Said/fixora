package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.customer.CustomerAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentJpaRepository extends JpaRepository<CustomerAttachment, Long> {
    List<CustomerAttachment> findByUserId(Long userId);
}

