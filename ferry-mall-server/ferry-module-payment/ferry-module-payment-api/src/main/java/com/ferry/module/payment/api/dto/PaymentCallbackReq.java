package com.ferry.module.payment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCallbackReq(
    @NotBlank String paymentNo,
    @NotBlank String orderNo,
    @NotBlank String thirdPartyNo,
    boolean success,
    String payload
) {}
