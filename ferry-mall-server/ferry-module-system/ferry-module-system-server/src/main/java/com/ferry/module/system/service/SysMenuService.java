package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.api.dto.MenuCreateReq;
import com.ferry.module.system.api.dto.MenuResp;
import com.ferry.module.system.dal.dataobject.SysMenuDO;
import com.ferry.module.system.dal.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuService(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public MenuResp create(MenuCreateReq req) {
        SysMenuDO menu = new SysMenuDO();
        menu.setName(req.name());
        menu.setPermission(req.permission());
        menu.setType(req.type() != null ? req.type() : 1);
        menu.setParentId(req.parentId() != null ? req.parentId() : 0L);
        menu.setSort(req.sort() != null ? req.sort() : 0);
        menu.setPath(req.path());
        menu.setComponent(req.component());
        menu.setIcon(req.icon());
        menu.setStatus(1);
        sysMenuMapper.insert(menu);
        return toResp(menu, List.of());
    }

    @Transactional(rollbackFor = Exception.class)
    public MenuResp update(Long id, MenuCreateReq req) {
        SysMenuDO menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new FerryBusinessException(404, "菜单不存在");
        }
        menu.setName(req.name());
        menu.setPermission(req.permission());
        menu.setType(req.type());
        menu.setParentId(req.parentId());
        menu.setSort(req.sort());
        menu.setPath(req.path());
        menu.setComponent(req.component());
        menu.setIcon(req.icon());
        sysMenuMapper.updateById(menu);
        return toResp(menu, List.of());
    }

    public MenuResp detail(Long id) {
        SysMenuDO menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new FerryBusinessException(404, "菜单不存在");
        }
        return toResp(menu, List.of());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysMenuMapper.deleteById(id);
    }

    public List<MenuResp> tree() {
        List<SysMenuDO> all = sysMenuMapper.selectList(
            new LambdaQueryWrapper<SysMenuDO>()
                .eq(SysMenuDO::getStatus, 1)
                .orderByAsc(SysMenuDO::getSort));
        return all.stream()
            .filter(m -> m.getParentId() == null || m.getParentId() == 0L)
            .map(m -> toTreeResp(m, all))
            .toList();
    }

    public List<MenuResp> listAll() {
        return sysMenuMapper.selectList(
            new LambdaQueryWrapper<SysMenuDO>()
                .orderByAsc(SysMenuDO::getSort))
            .stream().map(m -> toResp(m, List.of())).toList();
    }

    private MenuResp toTreeResp(SysMenuDO menu, List<SysMenuDO> all) {
        List<MenuResp> children = all.stream()
            .filter(c -> menu.getId().equals(c.getParentId()))
            .map(c -> toTreeResp(c, all))
            .toList();
        return toResp(menu, children);
    }

    private MenuResp toResp(SysMenuDO m, List<MenuResp> children) {
        return new MenuResp(m.getId(), m.getName(), m.getPermission(), m.getType(),
            m.getParentId(), m.getSort(), m.getPath(), m.getComponent(),
            m.getIcon(), m.getStatus(), children);
    }
}
