package com.ferry.module.system.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TenantCreateReq(
    @NotBlank String name,
    String contactName,
    String contactMobile,
    Long packageId,
    LocalDateTime expireTime,
    Integer accountCount
) {}
