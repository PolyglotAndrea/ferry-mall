package com.ferry.module.system.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MenuCreateReq(
    @NotBlank String name,
    String permission,
    Integer type,
    Long parentId,
    Integer sort,
    String path,
    String component,
    String icon
) {}
