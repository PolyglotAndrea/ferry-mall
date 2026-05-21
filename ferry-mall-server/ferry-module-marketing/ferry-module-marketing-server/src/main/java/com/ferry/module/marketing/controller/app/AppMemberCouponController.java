package com.ferry.module.marketing.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.marketing.dal.dataobject.MemberCouponDO;
import com.ferry.module.marketing.dal.mapper.MemberCouponMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/marketing/member-coupon")
public class AppMemberCouponController {

    private final MemberCouponMapper memberCouponMapper;

    public AppMemberCouponController(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
    }

    @GetMapping("/list")
    public CommonResult<List<MemberCouponDO>> list() {
        Long memberId = 10001L;
        List<MemberCouponDO> list = memberCouponMapper.selectList(
            new LambdaQueryWrapper<MemberCouponDO>()
                .eq(MemberCouponDO::getMemberId, memberId)
                .eq(MemberCouponDO::getStatus, 1)
                .orderByDesc(MemberCouponDO::getCreatedAt));
        return CommonResult.success(list);
    }
}
