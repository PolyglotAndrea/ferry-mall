package com.ferry.module.system.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateReq(@NotBlank String name, @NotBlank String code, Integer dataScope) {}
