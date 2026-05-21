package com.ferry.module.payment.api.dto;

import java.time.LocalDateTime;

public record RefundResp(
    Long id,
    String refundNo,
    String paymentNo,
    String orderNo,
    Integer refundAmountCent,
    Integer status,
    String statusText,
    String reason,
    LocalDateTime createdAt
) {}
