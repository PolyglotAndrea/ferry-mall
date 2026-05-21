package com.ferry.module.marketing.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.marketing.api.dto.CouponResp;
import com.ferry.module.marketing.service.MarketingCouponService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/marketing/coupon")
public class AppCouponController {
    private final MarketingCouponService marketingCouponService;

    public AppCouponController(MarketingCouponService marketingCouponService) {
        this.marketingCouponService = marketingCouponService;
    }

    @GetMapping("/available")
    public CommonResult<List<CouponResp>> available() {
        return CommonResult.success(marketingCouponService.available());
    }

    @PostMapping("/{id}/receive")
    public CommonResult<Boolean> receive(@PathVariable Long id) {
        return CommonResult.success(marketingCouponService.receive(id));
    }
}
