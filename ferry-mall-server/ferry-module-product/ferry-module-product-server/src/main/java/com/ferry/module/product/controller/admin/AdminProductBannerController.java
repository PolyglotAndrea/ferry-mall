package com.ferry.module.product.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.product.dal.dataobject.ProductBannerDO;
import com.ferry.module.product.dal.mapper.ProductBannerMapper;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-api/product/banner")
public class AdminProductBannerController {

    private final ProductBannerMapper productBannerMapper;

    public AdminProductBannerController(ProductBannerMapper productBannerMapper) {
        this.productBannerMapper = productBannerMapper;
    }

    @GetMapping("/list")
    @RequirePermission("product:banner:page")
    public CommonResult<List<ProductBannerDO>> list() {
        return CommonResult.success(productBannerMapper.selectList(null));
    }

    @PostMapping("/create")
    @RequirePermission("product:banner:create")
    public CommonResult<ProductBannerDO> create(@RequestParam String title,
                                                @RequestParam String imageUrl,
                                                @RequestParam String linkUrl,
                                                @RequestParam Integer sort) {
        ProductBannerDO banner = new ProductBannerDO();
        banner.setTitle(title);
        banner.setImageUrl(imageUrl);
        banner.setLinkUrl(linkUrl);
        banner.setSort(sort);
        banner.setStatus(1);
        productBannerMapper.insert(banner);
        return CommonResult.success(banner);
    }

    @PutMapping("/{id}/update")
    @RequirePermission("product:banner:update")
    public CommonResult<ProductBannerDO> update(@PathVariable Long id,
                                                @RequestParam String title,
                                                @RequestParam String imageUrl,
                                                @RequestParam String linkUrl,
                                                @RequestParam Integer sort) {
        ProductBannerDO banner = productBannerMapper.selectById(id);
        banner.setTitle(title);
        banner.setImageUrl(imageUrl);
        banner.setLinkUrl(linkUrl);
        banner.setSort(sort);
        productBannerMapper.updateById(banner);
        return CommonResult.success(banner);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("product:banner:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        productBannerMapper.deleteById(id);
        return CommonResult.success(true);
    }
}
