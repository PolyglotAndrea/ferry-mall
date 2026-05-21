package com.ferry.module.payment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefundCreateReq(
    @NotBlank String paymentNo,
    @NotBlank String orderNo,
    @NotNull Integer refundAmountCent,
    String reason
) {}
