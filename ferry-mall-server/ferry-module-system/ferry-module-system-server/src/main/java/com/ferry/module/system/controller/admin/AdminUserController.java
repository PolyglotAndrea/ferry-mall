package com.ferry.module.system.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.system.dal.dataobject.SysUserDO;
import com.ferry.module.system.dal.mapper.SysUserMapper;
import com.ferry.module.system.service.SysRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin-api/system/user")
public class AdminUserController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final SysRoleService sysRoleService;

    public AdminUserController(SysUserMapper sysUserMapper,
                               PasswordEncoder passwordEncoder,
                               SysRoleService sysRoleService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/page")
    @RequirePermission("system:user:page")
    public CommonResult<PageResult<SysUserDO>> page(
            @RequestParam(required = false) String keyword,
            PageParam pageParam) {
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDO::getTenantId, TenantContext.getTenantId());
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysUserDO::getUsername, keyword)
                              .or()
                              .like(SysUserDO::getNickname, keyword));
        }
        wrapper.orderByDesc(SysUserDO::getId);
        Page<SysUserDO> page = sysUserMapper.selectPage(
                new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return CommonResult.success(PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize()));
    }

    @GetMapping("/{id}")
    @RequirePermission("system:user:detail")
    public CommonResult<SysUserDO> detail(@PathVariable Long id) {
        SysUserDO user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new FerryBusinessException(404, "用户不存在");
        }
        user.setPassword(null);
        return CommonResult.success(user);
    }

    @PostMapping("/create")
    @RequirePermission("system:user:create")
    public CommonResult<SysUserDO> create(@Valid @RequestBody UserCreateReq req) {
        SysUserDO user = new SysUserDO();
        user.setTenantId(TenantContext.getTenantId());
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname());
        user.setDeptId(req.deptId() != null ? req.deptId() : 0L);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);
        user.setPassword(null);
        return CommonResult.success(user);
    }

    @PutMapping("/{id}/update")
    @RequirePermission("system:user:update")
    public CommonResult<SysUserDO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateReq req) {
        SysUserDO user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new FerryBusinessException(404, "用户不存在");
        }
        user.setNickname(req.nickname());
        user.setDeptId(req.deptId() != null ? req.deptId() : user.getDeptId());
        if (req.password() != null && !req.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.password()));
        }
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        user.setPassword(null);
        return CommonResult.success(user);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return CommonResult.success(true);
    }

    @PutMapping("/{id}/toggle-status")
    @RequirePermission("system:user:update")
    public CommonResult<Boolean> toggleStatus(@PathVariable Long id) {
        SysUserDO user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new FerryBusinessException(404, "用户不存在");
        }
        user.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        sysUserMapper.updateById(user);
        return CommonResult.success(true);
    }

    @PostMapping("/{id}/reset-password")
    @RequirePermission("system:user:update")
    public CommonResult<Boolean> resetPassword(@PathVariable Long id) {
        SysUserDO user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new FerryBusinessException(404, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return CommonResult.success(true);
    }

    @PostMapping("/{id}/assign-roles")
    @RequirePermission("system:user:update")
    public CommonResult<Boolean> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysRoleService.assignUserRoles(id, roleIds);
        return CommonResult.success(true);
    }

    public record UserCreateReq(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String nickname,
        Long deptId
    ) {}

    public record UserUpdateReq(
        String nickname,
        String password,
        Long deptId
    ) {}
}
