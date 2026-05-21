package com.ferry.module.system.api.dto;

import java.time.LocalDateTime;

public record TenantResp(
    Long id, String name, String contactName, String contactMobile,
    Long packageId, LocalDateTime expireTime, Integer accountCount,
    Integer status, LocalDateTime createdAt
) {}
