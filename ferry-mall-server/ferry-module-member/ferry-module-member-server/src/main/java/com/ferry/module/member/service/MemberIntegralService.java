package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberIntegralRecordDO;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberIntegralRecordMapper;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class MemberIntegralService {

    private static final String SIGN_KEY = "sign:";

    private final MemberIntegralRecordMapper memberIntegralRecordMapper;
    private final MemberUserMapper memberUserMapper;
    private final StringRedisTemplate redisTemplate;

    public MemberIntegralService(MemberIntegralRecordMapper memberIntegralRecordMapper,
                                  MemberUserMapper memberUserMapper,
                                  StringRedisTemplate redisTemplate) {
        this.memberIntegralRecordMapper = memberIntegralRecordMapper;
        this.memberUserMapper = memberUserMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long memberId, Integer points, String reason, Integer type) {
        MemberUserDO member = memberUserMapper.selectById(memberId);
        if (member == null) {
            throw new FerryBusinessException(404, "会员不存在");
        }
        int current = member.getPoints() != null ? member.getPoints() : 0;
        member.setPoints(current + points);
        memberUserMapper.updateById(member);

        MemberIntegralRecordDO record = new MemberIntegralRecordDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setMemberId(memberId);
        record.setChangeCount(points);
        record.setCurrentPoints(member.getPoints());
        record.setReason(reason);
        record.setType(type);
        record.setCreatedAt(LocalDateTime.now());
        memberIntegralRecordMapper.insert(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer sign(Long memberId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = SIGN_KEY + memberId + ":" + today;
        Boolean signed = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(signed)) {
            throw new FerryBusinessException(400, "今日已签到");
        }
        int points = 10;
        addPoints(memberId, points, "每日签到", 1);
        redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.DAYS);
        return points;
    }

    public PageResult<MemberIntegralRecordDO> records(Long memberId, PageParam pageParam) {
        Page<MemberIntegralRecordDO> page = memberIntegralRecordMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<MemberIntegralRecordDO>()
                .eq(MemberIntegralRecordDO::getMemberId, memberId)
                .orderByDesc(MemberIntegralRecordDO::getId));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }
}
