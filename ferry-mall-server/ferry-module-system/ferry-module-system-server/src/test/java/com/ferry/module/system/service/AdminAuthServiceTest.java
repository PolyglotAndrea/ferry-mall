package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.security.util.JwtTokenService;
import com.ferry.module.system.dal.dataobject.SysUserDO;
import com.ferry.module.system.dal.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AdminAuthService adminAuthService;

    @Test
    void login_success() {
        SysUserDO user = new SysUserDO();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("ferry123");
        user.setNickname("管理员");
        user.setStatus(1);

        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("ferry123", "ferry123")).thenReturn(true);

        var resp = adminAuthService.login(new com.ferry.module.system.api.dto.AdminLoginReq("admin", "ferry123"));

        assertNotNull(resp);
        assertEquals("管理员", resp.nickname());
    }
}
