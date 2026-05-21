package com.ferry.module.payment.api.dto;

import java.time.LocalDateTime;

public record PaymentRecordResp(
    Long id,
    String paymentNo,
    String orderNo,
    String channel,
    Integer amountCent,
    Integer status,
    String statusText,
    String thirdPartyNo,
    LocalDateTime paidAt,
    LocalDateTime createdAt
) {}
