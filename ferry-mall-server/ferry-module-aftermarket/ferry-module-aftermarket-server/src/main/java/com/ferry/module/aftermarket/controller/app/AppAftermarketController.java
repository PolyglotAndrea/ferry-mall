package com.ferry.module.aftermarket.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.aftermarket.api.dto.AftermarketApplyReq;
import com.ferry.module.aftermarket.api.dto.AftermarketResp;
import com.ferry.module.aftermarket.service.AftermarketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/aftermarket")
public class AppAftermarketController {
    private final AftermarketService aftermarketService;

    public AppAftermarketController(AftermarketService aftermarketService) {
        this.aftermarketService = aftermarketService;
    }

    @PostMapping("/apply")
    public CommonResult<AftermarketResp> apply(@Valid @RequestBody AftermarketApplyReq req) {
        return CommonResult.success(aftermarketService.apply(req));
    }
}
