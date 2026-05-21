package com.ferry.module.product.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateReq(
    @NotNull Long categoryId,
    @NotBlank String name,
    String subtitle,
    String coverUrl,
    @Min(1) Integer priceCent,
    @Min(0) Integer marketPriceCent,
    @Min(0) Integer stock,
    @NotNull Integer status
) {}
