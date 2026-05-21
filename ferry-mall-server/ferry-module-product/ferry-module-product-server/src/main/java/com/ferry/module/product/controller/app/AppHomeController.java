package com.ferry.module.product.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.product.dal.dataobject.ProductBannerDO;
import com.ferry.module.product.dal.dataobject.ProductSpuDO;
import com.ferry.module.product.service.ProductBannerService;
import com.ferry.module.product.service.ProductCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/home")
public class AppHomeController {

    private final ProductBannerService productBannerService;
    private final ProductCatalogService productCatalogService;

    public AppHomeController(ProductBannerService productBannerService,
                             ProductCatalogService productCatalogService) {
        this.productBannerService = productBannerService;
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/index")
    public CommonResult<HomeResp> index() {
        return CommonResult.success(new HomeResp(
            productBannerService.list(),
            productCatalogService.recommend(10)
        ));
    }

    public record HomeResp(
        List<ProductBannerDO> banners,
        List<ProductSpuDO> products
    ) {}
}
