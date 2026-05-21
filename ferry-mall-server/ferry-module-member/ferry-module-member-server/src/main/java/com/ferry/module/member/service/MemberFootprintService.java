package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberFootprintDO;
import com.ferry.module.member.dal.mapper.MemberFootprintMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberFootprintService {
    private final MemberFootprintMapper memberFootprintMapper;

    public MemberFootprintService(MemberFootprintMapper memberFootprintMapper) {
        this.memberFootprintMapper = memberFootprintMapper;
    }

    public List<MemberFootprintDO> list(Long memberId) {
        return memberFootprintMapper.selectList(new LambdaQueryWrapper<MemberFootprintDO>()
            .eq(MemberFootprintDO::getTenantId, TenantContext.getTenantId())
            .eq(MemberFootprintDO::getMemberId, memberId)
            .orderByDesc(MemberFootprintDO::getCreatedAt)
            .last("LIMIT 50"));
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberFootprintDO add(Long memberId, Long spuId, String spuName, String spuCover, Integer priceCent) {
        memberFootprintMapper.delete(new LambdaQueryWrapper<MemberFootprintDO>()
            .eq(MemberFootprintDO::getTenantId, TenantContext.getTenantId())
            .eq(MemberFootprintDO::getMemberId, memberId)
            .eq(MemberFootprintDO::getSpuId, spuId));

        MemberFootprintDO footprint = new MemberFootprintDO();
        footprint.setTenantId(TenantContext.getTenantId());
        footprint.setMemberId(memberId);
        footprint.setSpuId(spuId);
        footprint.setSpuName(spuName);
        footprint.setSpuCover(spuCover);
        footprint.setPriceCent(priceCent);
        footprint.setCreatedAt(LocalDateTime.now());
        memberFootprintMapper.insert(footprint);
        return footprint;
    }
}
