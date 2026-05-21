package com.ferry.module.store.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.store.api.dto.StoreResp;
import com.ferry.module.store.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/store")
public class AppStoreController {
    private final StoreService storeService;

    public AppStoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{id}")
    public CommonResult<StoreResp> detail(@PathVariable Long id) {
        return CommonResult.success(storeService.detail(id));
    }
}
