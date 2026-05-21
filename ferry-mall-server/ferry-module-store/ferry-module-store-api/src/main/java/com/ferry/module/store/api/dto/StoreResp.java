package com.ferry.module.store.api.dto;

public record StoreResp(Long id, Long merchantId, String name, String logoUrl, String description, Integer status, Double score) {}
