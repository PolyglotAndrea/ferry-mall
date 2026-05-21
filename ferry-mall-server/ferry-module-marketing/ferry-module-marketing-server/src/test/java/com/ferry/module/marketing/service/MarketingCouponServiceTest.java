package com.ferry.module.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.marketing.dal.dataobject.MarketingCouponDO;
import com.ferry.module.marketing.dal.mapper.MarketingCouponMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketingCouponServiceTest {

    @Mock
    private MarketingCouponMapper marketingCouponMapper;

    @InjectMocks
    private MarketingCouponService marketingCouponService;

    @Test
    void available_returnsActiveCoupons() {
        MarketingCouponDO coupon = new MarketingCouponDO();
        coupon.setId(1L);
        coupon.setName("满100减10");
        coupon.setDiscountCent(1000);
        coupon.setThresholdCent(10000);
        coupon.setStatus(1);

        when(marketingCouponMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of(coupon));

        var list = marketingCouponService.available();

        assertEquals(1, list.size());
        assertEquals("满100减10", list.get(0).name());
        assertEquals(1000, list.get(0).discountCent());
    }

    @Test
    void receive_success() {
        MarketingCouponDO coupon = new MarketingCouponDO();
        coupon.setId(1L);
        coupon.setStatus(1);
        coupon.setTotalCount(100);
        coupon.setReceivedCount(50);

        when(marketingCouponMapper.selectById(1L)).thenReturn(coupon);
        when(marketingCouponMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        assertTrue(marketingCouponService.receive(1L));
    }

    @Test
    void receive_couponExhausted_throws() {
        MarketingCouponDO coupon = new MarketingCouponDO();
        coupon.setId(1L);
        coupon.setStatus(1);
        coupon.setTotalCount(100);
        coupon.setReceivedCount(100);

        when(marketingCouponMapper.selectById(1L)).thenReturn(coupon);

        assertThrows(FerryBusinessException.class, () -> marketingCouponService.receive(1L));
    }
}
