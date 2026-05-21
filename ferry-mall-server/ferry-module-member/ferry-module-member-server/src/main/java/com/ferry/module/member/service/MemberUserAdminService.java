package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import com.ferry.module.order.dal.dataobject.OrderInfoDO;
import com.ferry.module.order.dal.mapper.OrderInfoMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberUserAdminService {

    private final MemberUserMapper memberUserMapper;
    private final OrderInfoMapper orderInfoMapper;

    public MemberUserAdminService(MemberUserMapper memberUserMapper,
                                   OrderInfoMapper orderInfoMapper) {
        this.memberUserMapper = memberUserMapper;
        this.orderInfoMapper = orderInfoMapper;
    }

    public PageResult<MemberUserPageItem> page(String keyword, PageParam pageParam) {
        LambdaQueryWrapper<MemberUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberUserDO::getTenantId, TenantContext.getTenantId());
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(MemberUserDO::getNickname, keyword)
                              .or()
                              .like(MemberUserDO::getMobile, keyword));
        }
        wrapper.orderByDesc(MemberUserDO::getId);

        Page<MemberUserDO> page = memberUserMapper.selectPage(
                new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);

        List<MemberUserPageItem> list = page.getRecords().stream().map(user -> {
            MemberUserPageItem item = new MemberUserPageItem();
            item.setId(user.getId());
            item.setNickname(user.getNickname());
            item.setAvatar(user.getAvatarUrl());
            item.setMobile(user.getMobile());
            item.setPoints(user.getPoints());
            item.setStatus(user.getStatus());

            MemberStats stats = computeStats(user.getId());
            item.setOrderCount(stats.getOrderCount());
            item.setTotalSpendCent(stats.getTotalSpendCent());

            return item;
        }).collect(Collectors.toList());

        return PageResult.of(list, page.getTotal(), pageParam.pageSize());
    }

    public MemberUserDO detail(Long id) {
        return memberUserMapper.selectById(id);
    }

    public MemberStats stats(Long memberId) {
        return computeStats(memberId);
    }

    private MemberStats computeStats(Long memberId) {
        List<OrderInfoDO> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfoDO>()
                        .eq(OrderInfoDO::getMemberId, memberId));

        MemberStats stats = new MemberStats();
        stats.setOrderCount(orders.size());
        int totalSpend = orders.stream()
                .mapToInt(o -> o.getPayAmountCent() != null ? o.getPayAmountCent() : 0)
                .sum();
        stats.setTotalSpendCent(totalSpend);
        return stats;
    }

    @Data
    public static class MemberUserPageItem {
        private Long id;
        private String nickname;
        private String avatar;
        private String mobile;
        private String level;
        private Integer points;
        private Integer orderCount;
        private Integer totalSpendCent;
        private Integer status;
    }

    @Data
    public static class MemberUserDetailResp {
        private Long id;
        private String nickname;
        private String avatar;
        private String mobile;
        private String level;
        private Long levelId;
        private Integer points;
        private Integer orderCount;
        private Integer totalSpendCent;
        private Integer status;
    }

    @Data
    public static class MemberStats {
        private Integer orderCount;
        private Integer totalSpendCent;
    }
}
