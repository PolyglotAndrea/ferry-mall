package com.ferry.module.merchant.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.merchant.api.dto.MerchantApplyReq;
import com.ferry.module.merchant.api.dto.MerchantResp;
import com.ferry.module.merchant.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/merchant")
public class AppMerchantController {
    private final MerchantService merchantService;

    public AppMerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/apply")
    public CommonResult<MerchantResp> apply(@Valid @RequestBody MerchantApplyReq req) {
        return CommonResult.success(merchantService.apply(req));
    }
}
