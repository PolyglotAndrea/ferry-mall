package com.ferry.module.order.api.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderCancelReq(@NotBlank String orderNo, String reason) {}
