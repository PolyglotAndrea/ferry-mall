package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.security.util.JwtTokenService;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.member.api.dto.MemberLoginReq;
import com.ferry.module.member.api.dto.MemberLoginResp;
import com.ferry.module.member.api.dto.MemberProfileResp;
import com.ferry.module.member.client.WxMiniappClient;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class MemberAuthService {
    private final MemberUserMapper memberUserMapper;
    private final WxMiniappClient wxMiniappClient;
    private final JwtTokenService jwtTokenService;

    public MemberAuthService(MemberUserMapper memberUserMapper,
                             WxMiniappClient wxMiniappClient,
                             JwtTokenService jwtTokenService) {
        this.memberUserMapper = memberUserMapper;
        this.wxMiniappClient = wxMiniappClient;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberLoginResp login(MemberLoginReq req) {
        var wxResp = wxMiniappClient.jscode2session(req.code());

        MemberUserDO member = memberUserMapper.selectOne(new LambdaQueryWrapper<MemberUserDO>()
            .eq(MemberUserDO::getOpenid, wxResp.openid())
            .last("limit 1"));

        if (member == null) {
            member = new MemberUserDO();
            member.setOpenid(wxResp.openid());
            member.setUnionid(wxResp.unionid());
            member.setNickname("微信用户" + wxResp.openid().substring(wxResp.openid().length() - 6));
            member.setPoints(0);
            member.setStatus(1);
            memberUserMapper.insert(member);
        }

        Long tenantId = member.getTenantId() != null ? member.getTenantId() : 0L;
        String token = jwtTokenService.createToken(
            tenantId + ":" + member.getId(), Duration.ofDays(7));

        return new MemberLoginResp(token, member.getId());
    }

    public MemberProfileResp profile() {
        MemberUserDO member = currentMember();
        return new MemberProfileResp(member.getId(), member.getNickname(), member.getAvatarUrl(), member.getPoints());
    }

    private MemberUserDO currentMember() {
        MemberUserDO member = memberUserMapper.selectOne(new LambdaQueryWrapper<MemberUserDO>()
            .eq(MemberUserDO::getStatus, 1)
            .orderByAsc(MemberUserDO::getId)
            .last("limit 1"));
        if (member == null) {
            throw new FerryBusinessException(404, "会员不存在");
        }
        return member;
    }
}
