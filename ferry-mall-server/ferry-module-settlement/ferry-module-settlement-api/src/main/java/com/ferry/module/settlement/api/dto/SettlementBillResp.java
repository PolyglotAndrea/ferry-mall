package com.ferry.module.settlement.api.dto;

public record SettlementBillResp(Long id, Long merchantId, String merchantName, Integer orderAmountCent, Integer commissionCent, Integer payableCent, Integer status, String statusText) {}
