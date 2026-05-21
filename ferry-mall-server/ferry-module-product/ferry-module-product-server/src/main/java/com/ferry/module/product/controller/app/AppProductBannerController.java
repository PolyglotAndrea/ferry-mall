package com.ferry.module.product.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.product.dal.dataobject.ProductBannerDO;
import com.ferry.module.product.service.ProductBannerService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/product/banner")
public class AppProductBannerController {
    private final ProductBannerService productBannerService;

    public AppProductBannerController(ProductBannerService productBannerService) {
        this.productBannerService = productBannerService;
    }

    @GetMapping("/list")
    public CommonResult<List<ProductBannerDO>> list() {
        return CommonResult.success(productBannerService.list());
    }
}
