package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberLevelDO;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberLevelMapper;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberLevelService {

    private final MemberLevelMapper memberLevelMapper;
    private final MemberUserMapper memberUserMapper;

    public MemberLevelService(MemberLevelMapper memberLevelMapper, MemberUserMapper memberUserMapper) {
        this.memberLevelMapper = memberLevelMapper;
        this.memberUserMapper = memberUserMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberLevelDO create(String name, Integer minPoints, Integer discountPercent) {
        MemberLevelDO level = new MemberLevelDO();
        level.setTenantId(TenantContext.getTenantId());
        level.setName(name);
        level.setMinPoints(minPoints);
        level.setDiscountPercent(discountPercent != null ? discountPercent : 100);
        level.setStatus(1);
        memberLevelMapper.insert(level);
        return level;
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberLevelDO update(Long id, String name, Integer minPoints, Integer discountPercent) {
        MemberLevelDO level = memberLevelMapper.selectById(id);
        if (level == null) {
            throw new FerryBusinessException(404, "会员等级不存在");
        }
        level.setName(name);
        level.setMinPoints(minPoints);
        level.setDiscountPercent(discountPercent);
        memberLevelMapper.updateById(level);
        return level;
    }

    public void delete(Long id) {
        memberLevelMapper.deleteById(id);
    }

    public MemberLevelDO detail(Long id) {
        MemberLevelDO level = memberLevelMapper.selectById(id);
        if (level == null) {
            throw new FerryBusinessException(404, "会员等级不存在");
        }
        return level;
    }

    public List<MemberLevelDO> list() {
        return memberLevelMapper.selectList(
            new LambdaQueryWrapper<MemberLevelDO>()
                .eq(MemberLevelDO::getStatus, 1)
                .orderByAsc(MemberLevelDO::getMinPoints));
    }

    public PageResult<MemberLevelDO> page(PageParam pageParam) {
        Page<MemberLevelDO> page = memberLevelMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<MemberLevelDO>()
                .eq(MemberLevelDO::getTenantId, TenantContext.getTenantId())
                .orderByAsc(MemberLevelDO::getMinPoints));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    public MemberLevelDO resolveLevel(Long memberId) {
        MemberUserDO member = memberUserMapper.selectById(memberId);
        if (member == null) {
            return null;
        }
        int points = member.getPoints() != null ? member.getPoints() : 0;
        List<MemberLevelDO> levels = memberLevelMapper.selectList(
            new LambdaQueryWrapper<MemberLevelDO>()
                .eq(MemberLevelDO::getStatus, 1)
                .le(MemberLevelDO::getMinPoints, points)
                .orderByDesc(MemberLevelDO::getMinPoints));
        return levels.isEmpty() ? null : levels.get(0);
    }
}
