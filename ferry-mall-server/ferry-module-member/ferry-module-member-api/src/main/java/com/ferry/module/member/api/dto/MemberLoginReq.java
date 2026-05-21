package com.ferry.module.member.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginReq(@NotBlank String code) {}
