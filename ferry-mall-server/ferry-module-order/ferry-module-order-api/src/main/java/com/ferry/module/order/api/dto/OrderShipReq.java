package com.ferry.module.order.api.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderShipReq(
    @NotBlank String orderNo,
    @NotBlank String logisticsNo,
    @NotBlank String logisticsCompany
) {}
