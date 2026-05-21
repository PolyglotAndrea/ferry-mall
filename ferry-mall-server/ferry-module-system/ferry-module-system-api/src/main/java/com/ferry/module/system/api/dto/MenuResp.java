package com.ferry.module.system.api.dto;

import java.util.List;

public record MenuResp(
    Long id, String name, String permission, Integer type,
    Long parentId, Integer sort, String path, String component,
    String icon, Integer status, List<MenuResp> children
) {}
