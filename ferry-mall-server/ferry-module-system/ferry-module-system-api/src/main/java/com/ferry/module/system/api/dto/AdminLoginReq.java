package com.ferry.module.system.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginReq(@NotBlank String username, @NotBlank String password) {}
