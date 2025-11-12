package com.fixora.maintainance.maintainancerequest.domain.model;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.entity.shared.Building;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Company {


    private Long id;


    private String name;

    private String email;
    private String phone;
    private String address;


    private LocalDateTime createdAt = LocalDateTime.now();


    private List<UserEntity> userEntities;

    private List<Building> buildings;

}
