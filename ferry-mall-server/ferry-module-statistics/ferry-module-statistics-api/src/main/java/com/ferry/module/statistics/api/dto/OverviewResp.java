package com.ferry.module.statistics.api.dto;

public record OverviewResp(
    Integer orderCount,
    Integer salesAmountCent,
    Integer memberCount,
    Integer productCount,
    Integer merchantCount
) {}
