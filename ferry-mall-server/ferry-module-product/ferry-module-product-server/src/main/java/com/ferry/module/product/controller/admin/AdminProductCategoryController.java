package com.ferry.module.product.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.product.dal.dataobject.ProductCategoryDO;
import com.ferry.module.product.dal.mapper.ProductCategoryMapper;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-api/product/category")
public class AdminProductCategoryController {

    private final ProductCategoryMapper productCategoryMapper;

    public AdminProductCategoryController(ProductCategoryMapper productCategoryMapper) {
        this.productCategoryMapper = productCategoryMapper;
    }

    @GetMapping("/tree")
    @RequirePermission("product:category:page")
    public CommonResult<List<ProductCategoryDO>> tree() {
        return CommonResult.success(productCategoryMapper.selectList(null));
    }

    @PostMapping("/create")
    @RequirePermission("product:category:create")
    public CommonResult<ProductCategoryDO> create(@RequestParam String name,
                                                   @RequestParam Long parentId,
                                                   @RequestParam Integer sort) {
        ProductCategoryDO cat = new ProductCategoryDO();
        cat.setName(name);
        cat.setParentId(parentId);
        cat.setSort(sort);
        cat.setVisible(1);
        productCategoryMapper.insert(cat);
        return CommonResult.success(cat);
    }

    @PutMapping("/{id}/update")
    @RequirePermission("product:category:update")
    public CommonResult<ProductCategoryDO> update(@PathVariable Long id,
                                                   @RequestParam String name,
                                                   @RequestParam Long parentId,
                                                   @RequestParam Integer sort) {
        ProductCategoryDO cat = productCategoryMapper.selectById(id);
        cat.setName(name);
        cat.setParentId(parentId);
        cat.setSort(sort);
        productCategoryMapper.updateById(cat);
        return CommonResult.success(cat);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("product:category:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        productCategoryMapper.deleteById(id);
        return CommonResult.success(true);
    }
}
