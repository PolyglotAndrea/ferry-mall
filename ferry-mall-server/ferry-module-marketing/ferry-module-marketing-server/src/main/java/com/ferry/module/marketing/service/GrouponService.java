package com.ferry.module.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.marketing.dal.dataobject.GrouponActivityDO;
import com.ferry.module.marketing.dal.dataobject.GrouponRecordDO;
import com.ferry.module.marketing.dal.mapper.GrouponActivityMapper;
import com.ferry.module.marketing.dal.mapper.GrouponRecordMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GrouponService {
    private final GrouponActivityMapper grouponActivityMapper;
    private final GrouponRecordMapper grouponRecordMapper;

    public GrouponService(GrouponActivityMapper grouponActivityMapper, GrouponRecordMapper grouponRecordMapper) {
        this.grouponActivityMapper = grouponActivityMapper;
        this.grouponRecordMapper = grouponRecordMapper;
    }

    public List<GrouponActivityDO> listActivities() {
        LocalDateTime now = LocalDateTime.now();
        return grouponActivityMapper.selectList(new LambdaQueryWrapper<GrouponActivityDO>()
            .eq(GrouponActivityDO::getTenantId, TenantContext.getTenantId())
            .eq(GrouponActivityDO::getStatus, 1)
            .le(GrouponActivityDO::getStartTime, now)
            .ge(GrouponActivityDO::getEndTime, now)
            .orderByDesc(GrouponActivityDO::getId));
    }

    public GrouponActivityDO getActivityDetail(Long activityId) {
        GrouponActivityDO activity = grouponActivityMapper.selectById(activityId);
        if (activity == null || !activity.getTenantId().equals(TenantContext.getTenantId())) {
            throw new FerryBusinessException(404, "拼团活动不存在");
        }
        long joinCount = grouponRecordMapper.selectCount(new LambdaQueryWrapper<GrouponRecordDO>()
            .eq(GrouponRecordDO::getActivityId, activityId)
            .in(GrouponRecordDO::getStatus, List.of(1, 2)));
        return activity;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean join(Long activityId, Long memberId) {
        GrouponActivityDO activity = grouponActivityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            throw new FerryBusinessException(404, "拼团活动不存在或已结束");
        }
        long existingCount = grouponRecordMapper.selectCount(new LambdaQueryWrapper<GrouponRecordDO>()
            .eq(GrouponRecordDO::getActivityId, activityId)
            .eq(GrouponRecordDO::getMemberId, memberId)
            .in(GrouponRecordDO::getStatus, List.of(1, 2)));
        if (existingCount > 0) {
            throw new FerryBusinessException(400, "您已参与该拼团活动");
        }
        GrouponRecordDO record = new GrouponRecordDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setActivityId(activityId);
        record.setMemberId(memberId);
        record.setStatus(1);
        grouponRecordMapper.insert(record);
        return true;
    }
}
