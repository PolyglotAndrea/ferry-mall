package com.ferry.module.merchant.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MerchantApplyReq(@NotBlank String name, @NotBlank String contactName, @NotBlank String contactMobile, String licenseNo) {}
