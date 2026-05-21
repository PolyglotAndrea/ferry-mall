package com.ferry.module.aftermarket.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AftermarketApplyReq(@NotNull Long orderId, @NotBlank String reason) {}
