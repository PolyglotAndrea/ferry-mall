package com.ferry.module.statistics.api.dto;

public record PendingCountResp(Integer pendingShip, Integer pendingAftermarket, Integer pendingPayment, Integer pendingSettlement) {}
