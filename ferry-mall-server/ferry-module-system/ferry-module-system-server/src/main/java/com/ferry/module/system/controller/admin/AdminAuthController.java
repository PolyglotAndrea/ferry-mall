package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.system.api.dto.AdminLoginReq;
import com.ferry.module.system.api.dto.AdminLoginResp;
import com.ferry.module.system.api.dto.AdminProfileResp;
import com.ferry.module.system.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/system/auth")
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public CommonResult<AdminLoginResp> login(@Valid @RequestBody AdminLoginReq req) {
        return CommonResult.success(adminAuthService.login(req));
    }

    @GetMapping("/profile")
    public CommonResult<AdminProfileResp> profile() {
        return CommonResult.success(adminAuthService.profile());
    }
}
