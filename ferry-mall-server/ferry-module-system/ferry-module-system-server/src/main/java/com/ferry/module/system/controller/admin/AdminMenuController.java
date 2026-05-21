package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.system.api.dto.MenuCreateReq;
import com.ferry.module.system.api.dto.MenuNodeResp;
import com.ferry.module.system.api.dto.MenuResp;
import com.ferry.module.system.service.AdminMenuService;
import com.ferry.module.system.service.SysMenuService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/system/menu")
public class AdminMenuController {
    private final AdminMenuService adminMenuService;
    private final SysMenuService sysMenuService;

    public AdminMenuController(AdminMenuService adminMenuService, SysMenuService sysMenuService) {
        this.adminMenuService = adminMenuService;
        this.sysMenuService = sysMenuService;
    }

    @GetMapping("/tree")
    public CommonResult<List<MenuNodeResp>> tree() {
        return CommonResult.success(adminMenuService.tree());
    }

    @GetMapping("/rbac-tree")
    public CommonResult<List<MenuResp>> rbacTree() {
        return CommonResult.success(sysMenuService.tree());
    }

    @GetMapping("/list")
    public CommonResult<List<MenuResp>> list() {
        return CommonResult.success(sysMenuService.listAll());
    }

    @GetMapping("/{id}")
    public CommonResult<MenuResp> detail(@PathVariable Long id) {
        return CommonResult.success(sysMenuService.detail(id));
    }

    @PostMapping("/create")
    public CommonResult<MenuResp> create(@Valid @RequestBody MenuCreateReq req) {
        return CommonResult.success(sysMenuService.create(req));
    }

    @PutMapping("/{id}/update")
    public CommonResult<MenuResp> update(@PathVariable Long id, @Valid @RequestBody MenuCreateReq req) {
        return CommonResult.success(sysMenuService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return CommonResult.success(true);
    }
}
