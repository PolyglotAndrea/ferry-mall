package com.ferry.module.product.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.api.dto.ProductCategoryResp;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.service.ProductCatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/product")
public class AppProductController {
    private final ProductCatalogService productCatalogService;

    public AppProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/category/tree")
    public CommonResult<List<ProductCategoryResp>> categoryTree() {
        return CommonResult.success(productCatalogService.categoryTree());
    }

    @GetMapping("/spu/page")
    public CommonResult<PageResult<ProductSpuSnapshot>> page(PageParam pageParam) {
        return CommonResult.success(productCatalogService.page(pageParam));
    }

    @GetMapping("/spu/{id}")
    public CommonResult<ProductSpuSnapshot> detail(@PathVariable Long id) {
        return CommonResult.success(productCatalogService.detail(id));
    }
}
