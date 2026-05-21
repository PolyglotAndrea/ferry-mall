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

    public CommissionOverview overview(Long memberId) {
        CommissionUserDO user = getPromoter(memberId);
        CommissionOverview ov = new CommissionOverview();
        if (user != null) {
            ov.totalCent = user.getTotalCommissionCent();
            ov.availableCent = user.getAvailableCommissionCent();
        }
        long teamSize = commissionUserMapper.selectCount(
            new LambdaQueryWrapper<CommissionUserDO>()
                .eq(CommissionUserDO::getParentId, memberId)
                .eq(CommissionUserDO::getTenantId, TenantContext.getTenantId()));
        ov.teamSize = (int) teamSize;
        return ov;
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long memberId, Integer amountCent) {
        if (amountCent <= 0) {
            throw new FerryBusinessException(400, "提现金额必须大于0");
        }
        CommissionUserDO user = getPromoter(memberId);
        if (user == null) {
            throw new FerryBusinessException(400, "未申请推广员");
        }
        if (user.getAvailableCommissionCent() < amountCent) {
            throw new FerryBusinessException(400, "可提现金额不足");
        }
        user.setAvailableCommissionCent(user.getAvailableCommissionCent() - amountCent);
        commissionUserMapper.updateById(user);

        CommissionRecordDO record = new CommissionRecordDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setMemberId(memberId);
        record.setCommissionCent(-amountCent.longValue());
        record.setType(2);
        record.setCreatedAt(LocalDateTime.now());
        commissionRecordMapper.insert(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindParent(Long memberId, Long parentId) {
        if (memberId.equals(parentId)) {
            throw new FerryBusinessException(400, "不能绑定自己为推荐人");
        }
        CommissionUserDO existing = getPromoter(memberId);
        if (existing != null && existing.getParentId() != null && existing.getParentId() > 0) {
            throw new FerryBusinessException(400, "已绑定推荐人");
        }
        if (existing == null) {
            CommissionUserDO user = new CommissionUserDO();
            user.setTenantId(TenantContext.getTenantId());
            user.setMemberId(memberId);
            user.setParentId(parentId);
            user.setTotalCommissionCent(0L);
            user.setAvailableCommissionCent(0L);
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            commissionUserMapper.insert(user);
        } else {
            existing.setParentId(parentId);
            commissionUserMapper.updateById(existing);
        }
    }

    public static class CommissionOverview {
        public Long totalCent;
        public Long availableCent;
        public Integer teamSize;
    }
}
