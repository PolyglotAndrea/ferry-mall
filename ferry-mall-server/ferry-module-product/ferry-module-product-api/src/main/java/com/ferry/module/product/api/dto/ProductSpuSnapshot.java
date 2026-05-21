package com.ferry.module.product.api.dto;

public record ProductSpuSnapshot(
    Long id,
    Long categoryId,
    Long storeId,
    String name,
    String subtitle,
    String coverUrl,
    Integer priceCent,
    Integer marketPriceCent,
    Integer stock,
    Integer sales,
    Integer status
) {}
