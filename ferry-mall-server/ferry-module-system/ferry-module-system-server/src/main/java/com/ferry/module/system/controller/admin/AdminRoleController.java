package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.system.api.dto.RoleCreateReq;
import com.ferry.module.system.api.dto.RoleResp;
import com.ferry.module.system.service.SysRoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin-api/system/role")
public class AdminRoleController {

    private final SysRoleService sysRoleService;

    public AdminRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<RoleResp>> page(PageParam pageParam) {
        return CommonResult.success(sysRoleService.page(pageParam));
    }

    @GetMapping("/{id}")
    public CommonResult<RoleResp> detail(@PathVariable Long id) {
        return CommonResult.success(sysRoleService.detail(id));
    }

    @PostMapping("/create")
    public CommonResult<RoleResp> create(@Valid @RequestBody RoleCreateReq req) {
        return CommonResult.success(sysRoleService.create(req));
    }

    @PutMapping("/{id}/update")
    public CommonResult<RoleResp> update(@PathVariable Long id, @Valid @RequestBody RoleCreateReq req) {
        return CommonResult.success(sysRoleService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return CommonResult.success(true);
    }

    @PostMapping("/{id}/assign-menus")
    public CommonResult<Boolean> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return CommonResult.success(true);
    }
}
