package com.ferry.module.order.api.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderPayReq(@NotBlank String orderNo) {}
