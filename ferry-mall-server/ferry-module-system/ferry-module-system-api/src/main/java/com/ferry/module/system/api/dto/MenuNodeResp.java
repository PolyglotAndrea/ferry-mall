package com.ferry.module.system.api.dto;

import java.util.List;

public record MenuNodeResp(String name, String path, String icon, List<MenuNodeResp> children) {}
