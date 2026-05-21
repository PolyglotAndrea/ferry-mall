package com.ferry.module.order.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateReq(
    @NotNull Long spuId,
    Long skuId,
    @Min(1) Integer quantity
) {}
