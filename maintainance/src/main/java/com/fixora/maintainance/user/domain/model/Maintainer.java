package com.fixora.maintainance.user.domain.model;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder

public class Maintainer {

    private User user;

    private BigDecimal rate;
    private Boolean availability;
    private Double latitude;
    private Double longitude;

    private String profileStatus;
}
