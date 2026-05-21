package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.CommissionRecordDO;
import com.ferry.module.member.dal.dataobject.CommissionUserDO;
import com.ferry.module.member.dal.dataobject.CommissionWithdrawDO;
import com.ferry.module.member.dal.mapper.CommissionRecordMapper;
import com.ferry.module.member.dal.mapper.CommissionUserMapper;
import com.ferry.module.member.dal.mapper.CommissionWithdrawMapper;
import com.ferry.module.member.api.CommissionApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommissionService implements CommissionApi {

    private final CommissionUserMapper commissionUserMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    private final CommissionWithdrawMapper commissionWithdrawMapper;

    public CommissionService(CommissionUserMapper commissionUserMapper,
                             CommissionRecordMapper commissionRecordMapper,
                             CommissionWithdrawMapper commissionWithdrawMapper) {
        this.commissionUserMapper = commissionUserMapper;
        this.commissionRecordMapper = commissionRecordMapper;
        this.commissionWithdrawMapper = commissionWithdrawMapper;
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

        long withdrawn = commissionWithdrawMapper.selectList(
            new LambdaQueryWrapper<CommissionWithdrawDO>()
                .eq(CommissionWithdrawDO::getMemberId, memberId)
                .eq(CommissionWithdrawDO::getTenantId, TenantContext.getTenantId())
                .eq(CommissionWithdrawDO::getStatus, 2))
            .stream().mapToLong(CommissionWithdrawDO::getAmountCent).sum();
        ov.withdrawnCent = withdrawn;
        return ov;
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long memberId, Integer amountCent) {
        if (amountCent == null || amountCent <= 0) {
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

        CommissionWithdrawDO withdraw = new CommissionWithdrawDO();
        withdraw.setTenantId(TenantContext.getTenantId());
        withdraw.setMemberId(memberId);
        withdraw.setAmountCent(amountCent);
        withdraw.setStatus(1);
        withdraw.setCreatedAt(LocalDateTime.now());
        withdraw.setUpdatedAt(LocalDateTime.now());
        commissionWithdrawMapper.insert(withdraw);
    }

    public PageResult<CommissionWithdrawDO> getWithdrawRecords(Long memberId, PageParam pageParam) {
        Page<CommissionWithdrawDO> page = commissionWithdrawMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<CommissionWithdrawDO>()
                .eq(CommissionWithdrawDO::getMemberId, memberId)
                .eq(CommissionWithdrawDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(CommissionWithdrawDO::getId));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindParent(Long memberId, Long parentId) {
        if (memberId.equals(parentId)) {
            throw new FerryBusinessException(400, "不能绑定自己为推荐人");
        }
        CommissionUserDO parent = getPromoter(parentId);
        if (parent == null) {
            throw new FerryBusinessException(400, "推荐人不存在");
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

    @Transactional(rollbackFor = Exception.class)
    public void calculateCommission(Long memberId, String orderNo, Integer payAmountCent) {
        if (payAmountCent == null || payAmountCent <= 0) {
            return;
        }
        CommissionUserDO user = getPromoter(memberId);
        if (user == null || user.getParentId() == null || user.getParentId() <= 0) {
            return;
        }
        CommissionUserDO parent = commissionUserMapper.selectById(user.getParentId());
        if (parent == null) {
            return;
        }
        int commissionCent = (int) (payAmountCent * 0.05);
        if (commissionCent <= 0) {
            return;
        }
        parent.setTotalCommissionCent(parent.getTotalCommissionCent() + commissionCent);
        parent.setAvailableCommissionCent(parent.getAvailableCommissionCent() + commissionCent);
        commissionUserMapper.updateById(parent);

        CommissionRecordDO record = new CommissionRecordDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setMemberId(parent.getMemberId());
        record.setOrderNo(orderNo);
        record.setCommissionCent((long) commissionCent);
        record.setType(1);
        record.setStatus(1);
        record.setCreatedAt(LocalDateTime.now());
        commissionRecordMapper.insert(record);
    }

    public static class CommissionOverview {
        public Long totalCent;
        public Long availableCent;
        public Long withdrawnCent;
        public Integer teamSize;
    }
}
