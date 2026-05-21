package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.security.util.JwtTokenService;
import com.ferry.module.member.client.WxMiniappClient;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {

    @Mock
    private MemberUserMapper memberUserMapper;

    @Mock
    private WxMiniappClient wxMiniappClient;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private MemberAuthService memberAuthService;

    @Test
    void login_existingUser() {
        when(wxMiniappClient.jscode2session("wx_code_123"))
            .thenReturn(new WxMiniappClient.JsCode2SessionResp("openid_123", "session_123", null));

        MemberUserDO member = new MemberUserDO();
        member.setId(1L);
        member.setOpenid("openid_123");
        member.setNickname("Ferry用户");
        member.setPoints(100);
        member.setStatus(1);

        when(memberUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(member);
        when(jwtTokenService.createToken(any(), any())).thenReturn("jwt-token-123");

        var req = new com.ferry.module.member.api.dto.MemberLoginReq("wx_code_123");
        var resp = memberAuthService.login(req);

        assertNotNull(resp);
        assertEquals(1L, resp.memberId());
        assertEquals("jwt-token-123", resp.accessToken());
    }

    @Test
    void login_newUser() {
        when(wxMiniappClient.jscode2session("wx_code_new"))
            .thenReturn(new WxMiniappClient.JsCode2SessionResp("openid_new", "session_new", null));

        when(memberUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(jwtTokenService.createToken(any(), any())).thenReturn("jwt-token-new");

        var req = new com.ferry.module.member.api.dto.MemberLoginReq("wx_code_new");
        var resp = memberAuthService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-token-new", resp.accessToken());
    }

    @Test
    void profile_success() {
        MemberUserDO member = new MemberUserDO();
        member.setId(1L);
        member.setNickname("Ferry用户");
        member.setAvatarUrl("https://example.com/avatar.png");
        member.setPoints(100);
        member.setStatus(1);

        when(memberUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(member);

        var resp = memberAuthService.profile();

        assertNotNull(resp);
        assertEquals("Ferry用户", resp.nickname());
        assertEquals(100, resp.points());
    }
}
