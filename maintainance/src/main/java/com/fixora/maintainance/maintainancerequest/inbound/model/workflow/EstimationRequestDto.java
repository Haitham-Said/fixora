package com.fixora.maintainance.maintainancerequest.inbound.model.workflow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Request body for POST .../estimation */
public record EstimationRequestDto(
        @NotNull @Positive BigDecimal amount,
        String note
) {
}
