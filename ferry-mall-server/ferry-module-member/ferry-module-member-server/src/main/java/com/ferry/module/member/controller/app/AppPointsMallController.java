package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.member.dal.dataobject.PointsExchangeDO;
import com.ferry.module.member.dal.dataobject.PointsProductDO;
import com.ferry.module.member.service.PointsMallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/products/page")
    public CommonResult<PageResult<PointsProductDO>> pageProducts(PageParam pageParam) {
        return CommonResult.success(pointsMallService.pageProducts(pageParam));
    }

    @GetMapping("/products/{id}")
    public CommonResult<PointsProductDO> getProductDetail(@PathVariable Long id) {
        return CommonResult.success(pointsMallService.getProductDetail(id));
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

    @GetMapping("/exchange-records")
    public CommonResult<PageResult<PointsExchangeDO>> pageExchangeRecords(PageParam pageParam) {
        return CommonResult.success(pointsMallService.pageExchangeRecords(10001L, pageParam));
    }
}
