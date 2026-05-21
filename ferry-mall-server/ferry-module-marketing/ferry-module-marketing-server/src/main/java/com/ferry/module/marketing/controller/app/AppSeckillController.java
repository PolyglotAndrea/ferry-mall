package com.ferry.module.marketing.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.marketing.dal.dataobject.SeckillActivityDO;
import com.ferry.module.marketing.dal.dataobject.SeckillProductDO;
import com.ferry.module.marketing.service.SeckillService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/marketing/seckill")
public class AppSeckillController {
    private final SeckillService seckillService;

    public AppSeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @GetMapping("/activities")
    public CommonResult<List<SeckillActivityDO>> listActivities() {
        return CommonResult.success(seckillService.listActivities());
    }

    @GetMapping("/{activityId}/products")
    public CommonResult<List<SeckillProductDO>> listProducts(@PathVariable Long activityId) {
        return CommonResult.success(seckillService.listProducts(activityId));
    }

    @GetMapping("/product/{productId}")
    public CommonResult<SeckillProductDO> getProductDetail(@PathVariable Long productId) {
        return CommonResult.success(seckillService.getProductDetail(productId));
    }
}
