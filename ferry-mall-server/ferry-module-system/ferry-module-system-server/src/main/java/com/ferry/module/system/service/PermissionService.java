package com.ferry.module.system.service;

import com.ferry.module.system.dal.dataobject.SysMenuDO;
import com.ferry.module.system.dal.mapper.SysMenuMapper;
import com.ferry.module.system.dal.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    public PermissionService(SysRoleMapper sysRoleMapper, SysMenuMapper sysMenuMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    public List<String> getUserPermissions(Long userId) {
        return sysMenuMapper.selectPermissionsByUserId(userId);
    }

    public List<SysMenuDO> getUserMenus(Long userId) {
        return sysMenuMapper.selectListByUserId(userId);
    }

    public boolean hasPermission(Long userId, String permission) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }
}
