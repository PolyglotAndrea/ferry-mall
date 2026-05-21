package com.ferry.module.order.api.dto;

public record OrderItemResp(
    Long spuId,
    Long skuId,
    String productName,
    String productImage,
    Integer priceCent,
    Integer quantity,
    Integer totalCent
) {}
