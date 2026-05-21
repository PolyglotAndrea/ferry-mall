package com.ferry.module.marketing.api;

public interface CouponApi {

    /**
     * 使用优惠券，返回抵扣金额（单位：分）
     *
     * @param memberId 会员ID
     * @param couponId 优惠券ID（member_coupon.id）
     * @param totalAmountCent 订单总金额（分）
     * @return 实际抵扣金额（分）
     */
    int useCoupon(Long memberId, Long couponId, int totalAmountCent);
}
