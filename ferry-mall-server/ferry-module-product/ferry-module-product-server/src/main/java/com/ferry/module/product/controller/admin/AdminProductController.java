package com.ferry.module.product.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.api.dto.ProductCreateReq;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.service.ProductCatalogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/product/spu")
public class AdminProductController {
    private final ProductCatalogService productCatalogService;

    public AdminProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<ProductSpuSnapshot>> page(@Valid PageParam pageParam) {
        return CommonResult.success(productCatalogService.page(pageParam));
    }

    @PostMapping("/create")
    public CommonResult<ProductSpuSnapshot> create(@Valid @RequestBody ProductCreateReq req) {
        return CommonResult.success(productCatalogService.create(req));
    }
}
