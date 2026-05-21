package com.ferry.module.aftermarket.api.dto;

import java.time.LocalDateTime;

public record AftermarketResp(Long id, Long orderId, String reason, Integer status, String statusText, LocalDateTime createdAt) {}
