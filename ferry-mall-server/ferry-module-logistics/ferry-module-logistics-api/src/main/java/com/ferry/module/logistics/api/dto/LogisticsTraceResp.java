package com.ferry.module.logistics.api.dto;

import java.util.List;

public record LogisticsTraceResp(String logisticsNo, String company, List<String> traces) {}
