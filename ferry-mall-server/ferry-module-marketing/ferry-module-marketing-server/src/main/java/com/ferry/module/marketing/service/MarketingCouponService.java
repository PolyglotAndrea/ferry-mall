package com.ferry.module.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.marketing.api.CouponApi;
import com.ferry.module.marketing.api.dto.CouponResp;
import com.ferry.module.marketing.dal.dataobject.MarketingCouponDO;
import com.ferry.module.marketing.dal.dataobject.MemberCouponDO;
import com.ferry.module.marketing.dal.mapper.MarketingCouponMapper;
import com.ferry.module.marketing.dal.mapper.MemberCouponMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketingCouponService implements CouponApi {
    private final MarketingCouponMapper marketingCouponMapper;
    private final MemberCouponMapper memberCouponMapper;

    public MarketingCouponService(MarketingCouponMapper marketingCouponMapper, MemberCouponMapper memberCouponMapper) {
        this.marketingCouponMapper = marketingCouponMapper;
        this.memberCouponMapper = memberCouponMapper;
    }

    public List<CouponResp> available() {
        return marketingCouponMapper.selectList(new LambdaQueryWrapper<MarketingCouponDO>()
                .eq(MarketingCouponDO::getStatus, 1)
                .apply("received_count < total_count")
                .orderByAsc(MarketingCouponDO::getThresholdCent)
                .orderByAsc(MarketingCouponDO::getId))
            .stream()
            .map(item -> new CouponResp(item.getId(), item.getName(), item.getDiscountCent(), item.getThresholdCent()))
            .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean receive(Long couponId) {
        MarketingCouponDO coupon = marketingCouponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new FerryBusinessException(404, "优惠券不存在或已下架");
        }
        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new FerryBusinessException(400, "优惠券已领完");
        }
        marketingCouponMapper.update(null, new LambdaUpdateWrapper<MarketingCouponDO>()
            .eq(MarketingCouponDO::getId, couponId)
            .setSql("received_count = received_count + 1"));

        Long memberId = 10001L;
        MemberCouponDO memberCoupon = new MemberCouponDO();
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCouponId(coupon.getId());
        memberCoupon.setCouponName(coupon.getName());
        memberCoupon.setDiscountCent(coupon.getDiscountCent());
        memberCoupon.setThresholdCent(coupon.getThresholdCent());
        memberCoupon.setStatus(1);
        memberCoupon.setExpireTime(LocalDateTime.now().plusDays(30));
        memberCouponMapper.insert(memberCoupon);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int useCoupon(Long memberId, Long couponId, int totalAmountCent) {
        MemberCouponDO memberCoupon = memberCouponMapper.selectOne(
            new LambdaQueryWrapper<MemberCouponDO>()
                .eq(MemberCouponDO::getId, couponId)
                .eq(MemberCouponDO::getMemberId, memberId));

        if (memberCoupon == null) {
            throw new FerryBusinessException(404, "优惠券不存在");
        }
        if (memberCoupon.getStatus() != 1) {
            throw new FerryBusinessException(400, "优惠券已被使用或已过期");
        }
        if (totalAmountCent < memberCoupon.getThresholdCent()) {
            throw new FerryBusinessException(400,
                "订单金额未满¥" + (memberCoupon.getThresholdCent() / 100.0) + "，无法使用该优惠券");
        }

        // 标记为已使用
        memberCoupon.setStatus(2);
        memberCoupon.setUsedTime(LocalDateTime.now());
        memberCouponMapper.updateById(memberCoupon);

        return memberCoupon.getDiscountCent();
    }
}
