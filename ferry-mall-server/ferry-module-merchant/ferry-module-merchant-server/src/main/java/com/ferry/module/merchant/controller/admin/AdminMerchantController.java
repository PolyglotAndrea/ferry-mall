package com.ferry.module.merchant.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.merchant.api.dto.MerchantResp;
import com.ferry.module.merchant.service.MerchantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/merchant")
public class AdminMerchantController {
    private final MerchantService merchantService;

    public AdminMerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<MerchantResp>> page(PageParam pageParam) {
        return CommonResult.success(merchantService.page(pageParam));
    }

    @PostMapping("/{id}/approve")
    public CommonResult<MerchantResp> approve(@PathVariable Long id) {
        return CommonResult.success(merchantService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public CommonResult<MerchantResp> reject(@PathVariable Long id) {
        return CommonResult.success(merchantService.reject(id));
    }

    @GetMapping("/{id}")
    public CommonResult<MerchantResp> detail(@PathVariable Long id) {
        return CommonResult.success(merchantService.detail(id));
    }
}
