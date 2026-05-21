package com.ferry.module.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.marketing.dal.dataobject.BargainActivityDO;
import com.ferry.module.marketing.dal.dataobject.BargainRecordDO;
import com.ferry.module.marketing.dal.mapper.BargainActivityMapper;
import com.ferry.module.marketing.dal.mapper.BargainRecordMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BargainService {
    private final BargainActivityMapper bargainActivityMapper;
    private final BargainRecordMapper bargainRecordMapper;

    public BargainService(BargainActivityMapper bargainActivityMapper, BargainRecordMapper bargainRecordMapper) {
        this.bargainActivityMapper = bargainActivityMapper;
        this.bargainRecordMapper = bargainRecordMapper;
    }

    public List<BargainActivityDO> listActivities() {
        LocalDateTime now = LocalDateTime.now();
        return bargainActivityMapper.selectList(new LambdaQueryWrapper<BargainActivityDO>()
            .eq(BargainActivityDO::getTenantId, TenantContext.getTenantId())
            .eq(BargainActivityDO::getStatus, 1)
            .le(BargainActivityDO::getStartTime, now)
            .ge(BargainActivityDO::getEndTime, now)
            .orderByDesc(BargainActivityDO::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    public BargainRecordDO startBargain(Long activityId, Long memberId) {
        BargainActivityDO activity = bargainActivityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            throw new FerryBusinessException(404, "砍价活动不存在或已结束");
        }
        long existingCount = bargainRecordMapper.selectCount(new LambdaQueryWrapper<BargainRecordDO>()
            .eq(BargainRecordDO::getActivityId, activityId)
            .eq(BargainRecordDO::getMemberId, memberId)
            .eq(BargainRecordDO::getStatus, 1));
        if (existingCount > 0) {
            throw new FerryBusinessException(400, "您已有进行中的砍价");
        }
        int minPercent = 10;
        int maxPercent = 30;
        int cutPercent = ThreadLocalRandom.current().nextInt(minPercent, maxPercent + 1);
        int cutAmount = activity.getOriginalPriceCent() * cutPercent / 100;
        int currentPrice = activity.getOriginalPriceCent() - cutAmount;
        if (currentPrice < activity.getFloorPriceCent()) {
            currentPrice = activity.getFloorPriceCent();
        }
        BargainRecordDO record = new BargainRecordDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setActivityId(activityId);
        record.setMemberId(memberId);
        record.setCurrentPriceCent(currentPrice);
        record.setStatus(currentPrice <= activity.getFloorPriceCent() ? 2 : 1);
        bargainRecordMapper.insert(record);
        return record;
    }

    @Transactional(rollbackFor = Exception.class)
    public BargainRecordDO helpBargain(Long recordId, Long memberId) {
        BargainRecordDO record = bargainRecordMapper.selectById(recordId);
        if (record == null || !record.getTenantId().equals(TenantContext.getTenantId())) {
            throw new FerryBusinessException(404, "砍价记录不存在");
        }
        if (record.getStatus() != 1) {
            throw new FerryBusinessException(400, "该砍价已结束");
        }
        BargainActivityDO activity = bargainActivityMapper.selectById(record.getActivityId());
        if (activity == null) {
            throw new FerryBusinessException(404, "砍价活动不存在");
        }
        int minPercent = 1;
        int maxPercent = 5;
        int cutPercent = ThreadLocalRandom.current().nextInt(minPercent, maxPercent + 1);
        int cutAmount = activity.getOriginalPriceCent() * cutPercent / 100;
        if (cutAmount < 1) {
            cutAmount = 1;
        }
        int newPrice = record.getCurrentPriceCent() - cutAmount;
        if (newPrice < activity.getFloorPriceCent()) {
            newPrice = activity.getFloorPriceCent();
        }
        record.setCurrentPriceCent(newPrice);
        if (newPrice <= activity.getFloorPriceCent()) {
            record.setStatus(2);
        }
        bargainRecordMapper.updateById(record);
        return record;
    }

    public BargainRecordDO getRecord(Long recordId) {
        BargainRecordDO record = bargainRecordMapper.selectById(recordId);
        if (record == null || !record.getTenantId().equals(TenantContext.getTenantId())) {
            throw new FerryBusinessException(404, "砍价记录不存在");
        }
        return record;
    }
}
