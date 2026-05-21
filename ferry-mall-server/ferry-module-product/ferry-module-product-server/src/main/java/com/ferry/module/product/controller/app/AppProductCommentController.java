package com.ferry.module.product.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.dal.dataobject.ProductCommentDO;
import com.ferry.module.product.service.ProductCommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/product/comment")
public class AppProductCommentController {
    private final ProductCommentService productCommentService;

    public AppProductCommentController(ProductCommentService productCommentService) {
        this.productCommentService = productCommentService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<ProductCommentDO>> page(@RequestParam Long spuId, PageParam pageParam) {
        return CommonResult.success(productCommentService.pageBySpu(spuId, pageParam));
    }

    @PostMapping("/create")
    public CommonResult<ProductCommentDO> create(@RequestParam Long spuId,
                                                  @RequestParam String content,
                                                  @RequestParam Integer rating,
                                                  @RequestParam(required = false) String images) {
        return CommonResult.success(productCommentService.create(
            spuId, 10001L, "nickname", "avatar", content, rating, images));
    }
}
