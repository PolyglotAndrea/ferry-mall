package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.security.util.JwtTokenService;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.api.dto.AdminLoginReq;
import com.ferry.module.system.api.dto.AdminLoginResp;
import com.ferry.module.system.api.dto.AdminProfileResp;
import com.ferry.module.system.dal.dataobject.SysUserDO;
import com.ferry.module.system.dal.mapper.SysUserMapper;
import java.time.Duration;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {
    private static final List<String> DEFAULT_PERMISSIONS = List.of("system:manage", "product:manage", "order:manage");

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AdminAuthService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder,
                            JwtTokenService jwtTokenService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public AdminLoginResp login(AdminLoginReq req) {
        SysUserDO user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserDO>()
            .eq(SysUserDO::getUsername, req.username())
            .eq(SysUserDO::getStatus, 1)
            .last("limit 1"));
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new FerryBusinessException(401, "账号或密码错误");
        }
        Long tenantId = user.getTenantId() != null ? user.getTenantId() : 0L;
        String token = jwtTokenService.createToken(tenantId + ":" + user.getId(), Duration.ofHours(24));
        return new AdminLoginResp(token, user.getNickname());
    }

    public AdminProfileResp profile() {
        SysUserDO user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserDO>()
            .eq(SysUserDO::getStatus, 1)
            .orderByAsc(SysUserDO::getId)
            .last("limit 1"));
        if (user == null) {
            throw new FerryBusinessException(404, "管理员不存在");
        }
        return new AdminProfileResp(user.getId(), user.getUsername(), user.getNickname(), DEFAULT_PERMISSIONS);
    }
}
