package com.ferry.module.aftermarket.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.aftermarket.api.dto.AftermarketResp;
import com.ferry.module.aftermarket.service.AftermarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/aftermarket")
public class AdminAftermarketController {
    private final AftermarketService aftermarketService;

    public AdminAftermarketController(AftermarketService aftermarketService) {
        this.aftermarketService = aftermarketService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<AftermarketResp>> page(PageParam pageParam) {
        return CommonResult.success(aftermarketService.page(pageParam));
    }

    @PostMapping("/{id}/approve")
    public CommonResult<AftermarketResp> approve(@PathVariable Long id) {
        return CommonResult.success(aftermarketService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public CommonResult<AftermarketResp> reject(@PathVariable Long id, @RequestParam String reason) {
        return CommonResult.success(aftermarketService.reject(id, reason));
    }

    @PostMapping("/{id}/complete")
    public CommonResult<AftermarketResp> complete(@PathVariable Long id) {
        return CommonResult.success(aftermarketService.complete(id));
    }
}
