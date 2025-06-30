package com.fixora.maintainance.maintainancerequest.domain.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Maintainer {

    private User user;

    private BigDecimal rate;
    private Boolean availability;
    private Double latitude;
    private Double longitude;

    private String profileStatus;
}
