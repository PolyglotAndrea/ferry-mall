package com.ferry.module.product.api.dto;

public record ProductSpuResp(Long id, Long categoryId, String name, String subtitle, String coverUrl, Integer priceCent, Integer marketPriceCent, Integer stock, Integer sales) {}
