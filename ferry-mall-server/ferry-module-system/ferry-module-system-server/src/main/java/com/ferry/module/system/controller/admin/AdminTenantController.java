package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.system.api.dto.TenantCreateReq;
import com.ferry.module.system.api.dto.TenantResp;
import com.ferry.module.system.service.SysTenantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/system/tenant")
public class AdminTenantController {

    private final SysTenantService sysTenantService;

    public AdminTenantController(SysTenantService sysTenantService) {
        this.sysTenantService = sysTenantService;
    }

    @GetMapping("/page")
    @RequirePermission("system:tenant:page")
    public CommonResult<PageResult<TenantResp>> page(PageParam pageParam) {
        return CommonResult.success(sysTenantService.page(pageParam));
    }

    @GetMapping("/{id}")
    @RequirePermission("system:tenant:detail")
    public CommonResult<TenantResp> detail(@PathVariable Long id) {
        return CommonResult.success(sysTenantService.detail(id));
    }

    @PostMapping("/create")
    @RequirePermission("system:tenant:create")
    public CommonResult<TenantResp> create(@Valid @RequestBody TenantCreateReq req) {
        return CommonResult.success(sysTenantService.create(req));
    }

    @PutMapping("/{id}/update")
    @RequirePermission("system:tenant:update")
    public CommonResult<TenantResp> update(@PathVariable Long id, @Valid @RequestBody TenantCreateReq req) {
        return CommonResult.success(sysTenantService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:tenant:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        sysTenantService.delete(id);
        return CommonResult.success(true);
    }
}
