package com.fixora.maintainance.maintainancerequest.domain.model;

import com.fixora.maintainance.user.infrastructure.entity.shared.Company;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Building {

    private Long id;
    private String name;
    private String address;


}
