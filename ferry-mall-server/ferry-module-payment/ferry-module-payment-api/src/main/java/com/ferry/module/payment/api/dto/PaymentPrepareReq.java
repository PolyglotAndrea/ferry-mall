package com.ferry.module.payment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentPrepareReq(@NotBlank String orderNo, String channelCode) {}
