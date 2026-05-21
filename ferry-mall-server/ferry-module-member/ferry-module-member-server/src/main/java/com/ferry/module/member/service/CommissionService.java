package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.CommissionRecordDO;
import com.ferry.module.member.dal.dataobject.CommissionUserDO;
import com.ferry.module.member.dal.mapper.CommissionRecordMapper;
import com.ferry.module.member.dal.mapper.CommissionUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommissionService {

    private final CommissionUserMapper commissionUserMapper;
    private final CommissionRecordMapper commissionRecordMapper;

    public CommissionService(CommissionUserMapper commissionUserMapper,
                             CommissionRecordMapper commissionRecordMapper) {
        this.commissionUserMapper = commissionUserMapper;
        this.commissionRecordMapper = commissionRecordMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void apply(Long memberId) {
        CommissionUserDO existing = commissionUserMapper.selectOne(
            new LambdaQueryWrapper<CommissionUserDO>()
                .eq(CommissionUserDO::getMemberId, memberId)
                .eq(CommissionUserDO::getTenantId, TenantContext.getTenantId()));
        if (existing != null) {
            throw new FerryBusinessException(400, "已申请过推广员");
        }
        CommissionUserDO user = new CommissionUserDO();
        user.setTenantId(TenantContext.getTenantId());
        user.setMemberId(memberId);
        user.setParentId(0L);
        user.setTotalCommissionCent(0L);
        user.setAvailableCommissionCent(0L);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        commissionUserMapper.insert(user);
    }

    public CommissionUserDO getPromoter(Long memberId) {
        return commissionUserMapper.selectOne(
            new LambdaQueryWrapper<CommissionUserDO>()
                .eq(CommissionUserDO::getMemberId, memberId)
                .eq(CommissionUserDO::getTenantId, TenantContext.getTenantId()));
    }

    public PageResult<CommissionRecordDO> getRecords(Long memberId, PageParam pageParam) {
        Page<CommissionRecordDO> page = commissionRecordMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<CommissionRecordDO>()
                .eq(CommissionRecordDO::getMemberId, memberId)
                .eq(CommissionRecordDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(CommissionRecordDO::getId));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    public List<CommissionUserDO> getTeam(Long memberId) {
        return commissionUserMapper.selectList(
            new LambdaQueryWrapper<CommissionUserDO>()
                .eq(CommissionUserDO::getParentId, memberId)
                .eq(CommissionUserDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(CommissionUserDO::getId));
    }
}
