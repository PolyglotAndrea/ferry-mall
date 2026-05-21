package com.ferry.module.system.api.dto;

import java.util.List;

public record AdminProfileResp(Long id, String username, String nickname, List<String> permissions) {}
