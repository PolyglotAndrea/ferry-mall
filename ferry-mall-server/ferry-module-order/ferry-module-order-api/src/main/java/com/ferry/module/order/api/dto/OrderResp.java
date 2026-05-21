package com.ferry.module.order.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResp(
    Long id,
    String orderNo,
    Integer totalAmountCent,
    Integer discountAmountCent,
    Integer payAmountCent,
    Integer status,
    String statusText,
    String receiverName,
    String receiverMobile,
    String receiverAddress,
    String remark,
    String logisticsCompany,
    String logisticsNo,
    LocalDateTime payTime,
    LocalDateTime deliveryTime,
    LocalDateTime receiveTime,
    LocalDateTime cancelTime,
    String cancelReason,
    LocalDateTime createdAt,
    List<OrderItemResp> items
) {}
