package com.ferry.module.member.api;

public interface CommissionApi {
    void calculateCommission(Long memberId, String orderNo, Integer payAmountCent);
}
