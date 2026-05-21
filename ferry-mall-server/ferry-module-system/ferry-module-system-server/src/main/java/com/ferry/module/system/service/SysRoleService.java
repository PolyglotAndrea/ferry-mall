package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.system.api.dto.RoleCreateReq;
import com.ferry.module.system.api.dto.RoleResp;
import com.ferry.module.system.dal.dataobject.SysRoleDO;
import com.ferry.module.system.dal.dataobject.SysRoleMenuDO;
import com.ferry.module.system.dal.dataobject.SysUserRoleDO;
import com.ferry.module.system.dal.mapper.SysRoleMapper;
import com.ferry.module.system.dal.mapper.SysRoleMenuMapper;
import com.ferry.module.system.dal.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleService(SysRoleMapper sysRoleMapper,
                          SysUserRoleMapper sysUserRoleMapper,
                          SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleResp create(RoleCreateReq req) {
        SysRoleDO role = new SysRoleDO();
        role.setTenantId(TenantContext.getTenantId());
        role.setName(req.name());
        role.setCode(req.code());
        role.setDataScope(req.dataScope() != null ? req.dataScope() : 1);
        role.setStatus(1);
        sysRoleMapper.insert(role);
        return toResp(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleResp update(Long id, RoleCreateReq req) {
        SysRoleDO role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new FerryBusinessException(404, "角色不存在");
        }
        role.setName(req.name());
        role.setCode(req.code());
        if (req.dataScope() != null) {
            role.setDataScope(req.dataScope());
        }
        sysRoleMapper.updateById(role);
        return toResp(role);
    }

    public RoleResp detail(Long id) {
        SysRoleDO role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new FerryBusinessException(404, "角色不存在");
        }
        return toResp(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysRoleMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleDO>().eq(SysUserRoleDO::getRoleId, id));
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenuDO>().eq(SysRoleMenuDO::getRoleId, id));
    }

    public PageResult<RoleResp> page(PageParam pageParam) {
        Page<SysRoleDO> page = sysRoleMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<SysRoleDO>()
                .eq(SysRoleDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(SysRoleDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenuDO>().eq(SysRoleMenuDO::getRoleId, roleId));
        for (Long menuId : menuIds) {
            SysRoleMenuDO rm = new SysRoleMenuDO();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            sysRoleMenuMapper.insert(rm);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleDO>().eq(SysUserRoleDO::getUserId, userId));
        for (Long roleId : roleIds) {
            SysUserRoleDO ur = new SysUserRoleDO();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
    }

    private RoleResp toResp(SysRoleDO role) {
        return new RoleResp(role.getId(), role.getName(), role.getCode(), role.getDataScope(), role.getStatus());
    }
}
