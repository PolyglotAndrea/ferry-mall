package com.ferry.module.product.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockDeductReq(@NotNull Long spuId, @Min(1) Integer quantity) {}
