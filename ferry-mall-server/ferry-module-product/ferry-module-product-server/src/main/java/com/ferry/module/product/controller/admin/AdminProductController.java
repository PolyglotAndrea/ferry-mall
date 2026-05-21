package com.ferry.module.product.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.api.dto.ProductCreateReq;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.api.dto.ProductUpdateReq;
import com.ferry.module.product.service.ProductCatalogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/product/spu")
public class AdminProductController {
    private final ProductCatalogService productCatalogService;

    public AdminProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<ProductSpuSnapshot>> page(
            @Valid PageParam pageParam,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return CommonResult.success(productCatalogService.page(pageParam, keyword, categoryId));
    }

    @PostMapping("/create")
    public CommonResult<ProductSpuSnapshot> create(@Valid @RequestBody ProductCreateReq req) {
        return CommonResult.success(productCatalogService.create(req));
    }

    @PutMapping("/{id}/update")
    public CommonResult<ProductSpuSnapshot> update(@PathVariable Long id,
                                                   @Valid @RequestBody ProductUpdateReq req) {
        return CommonResult.success(productCatalogService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        productCatalogService.delete(id);
        return CommonResult.success(true);
    }

    @PutMapping("/{id}/toggle-status")
    public CommonResult<ProductSpuSnapshot> toggleStatus(@PathVariable Long id) {
        return CommonResult.success(productCatalogService.toggleStatus(id));
    }
}
