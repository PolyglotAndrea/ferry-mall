package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-api/system/user-role")
public class AdminUserRoleController {

    private final SysRoleService sysRoleService;

    public AdminUserRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @PostMapping("/{userId}/assign-roles")
    public CommonResult<Boolean> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        sysRoleService.assignUserRoles(userId, roleIds);
        return CommonResult.success(true);
    }
}
