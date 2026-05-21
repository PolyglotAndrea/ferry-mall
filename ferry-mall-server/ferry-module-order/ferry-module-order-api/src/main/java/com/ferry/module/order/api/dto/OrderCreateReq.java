package com.ferry.module.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateReq(
    @NotEmpty @Valid List<OrderItemCreateReq> items,
    @NotBlank String receiverName,
    @NotBlank String receiverMobile,
    @NotBlank String receiverAddress,
    String remark,
    Long couponId
) {}
