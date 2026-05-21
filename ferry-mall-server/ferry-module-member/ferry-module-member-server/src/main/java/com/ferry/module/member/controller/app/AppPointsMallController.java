package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.PointsExchangeDO;
import com.ferry.module.member.dal.dataobject.PointsProductDO;
import com.ferry.module.member.service.PointsMallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/member/points-mall")
public class AppPointsMallController {

    private final PointsMallService pointsMallService;

    public AppPointsMallController(PointsMallService pointsMallService) {
        this.pointsMallService = pointsMallService;
    }

    @GetMapping("/products")
    public CommonResult<List<PointsProductDO>> listProducts() {
        return CommonResult.success(pointsMallService.listProducts());
    }

    @PostMapping("/exchange")
    public CommonResult<Void> exchange(@RequestParam Long productId) {
        pointsMallService.exchange(10001L, productId);
        return CommonResult.success(null);
    }

    @GetMapping("/exchanges")
    public CommonResult<List<PointsExchangeDO>> getExchanges() {
        return CommonResult.success(pointsMallService.getExchanges(10001L));
    }
}
