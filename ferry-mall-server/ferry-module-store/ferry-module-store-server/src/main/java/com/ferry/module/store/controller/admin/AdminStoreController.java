package com.ferry.module.store.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.store.api.dto.StoreResp;
import com.ferry.module.store.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/store")
public class AdminStoreController {
    private final StoreService storeService;

    public AdminStoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<StoreResp>> page(PageParam pageParam) {
        return CommonResult.success(storeService.page(pageParam));
    }

    @PostMapping("/create")
    public CommonResult<StoreResp> create(@RequestParam Long merchantId,
                                          @RequestParam String name,
                                          @RequestParam(required = false) String logoUrl,
                                          @RequestParam(required = false) String description) {
        return CommonResult.success(storeService.create(merchantId, name, logoUrl, description));
    }

    @PutMapping("/{id}/update")
    public CommonResult<StoreResp> update(@PathVariable Long id,
                                          @RequestParam String name,
                                          @RequestParam(required = false) String logoUrl,
                                          @RequestParam(required = false) String description) {
        return CommonResult.success(storeService.update(id, name, logoUrl, description));
    }
}
