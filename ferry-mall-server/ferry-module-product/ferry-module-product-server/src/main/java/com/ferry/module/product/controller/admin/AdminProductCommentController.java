package com.ferry.module.product.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.dal.dataobject.ProductCommentDO;
import com.ferry.module.product.dal.mapper.ProductCommentMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-api/product/comment")
public class AdminProductCommentController {

    private final ProductCommentMapper productCommentMapper;

    public AdminProductCommentController(ProductCommentMapper productCommentMapper) {
        this.productCommentMapper = productCommentMapper;
    }

    @GetMapping("/page")
    @RequirePermission("product:comment:page")
    public CommonResult<PageResult<ProductCommentDO>> page(PageParam pageParam) {
        Page<ProductCommentDO> page = productCommentMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()), null);
        return CommonResult.success(PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize()));
    }

    @PostMapping("/{id}/audit")
    @RequirePermission("product:comment:audit")
    public CommonResult<ProductCommentDO> audit(@PathVariable Long id) {
        ProductCommentDO comment = productCommentMapper.selectById(id);
        comment.setStatus(1);
        productCommentMapper.updateById(comment);
        return CommonResult.success(comment);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("product:comment:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        productCommentMapper.deleteById(id);
        return CommonResult.success(true);
    }
}
