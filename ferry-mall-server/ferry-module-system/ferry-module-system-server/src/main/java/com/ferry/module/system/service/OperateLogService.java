package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.system.dal.dataobject.SysOperateLogDO;
import com.ferry.module.system.dal.mapper.SysOperateLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OperateLogService {

    private final SysOperateLogMapper sysOperateLogMapper;

    public OperateLogService(SysOperateLogMapper sysOperateLogMapper) {
        this.sysOperateLogMapper = sysOperateLogMapper;
    }

    @Async
    public void createLog(SysOperateLogDO log) {
        log.setTenantId(TenantContext.getTenantId());
        sysOperateLogMapper.insert(log);
    }

    public PageResult<SysOperateLogDO> page(String module, String name, Integer result,
                                             LocalDateTime startTime, LocalDateTime endTime,
                                             PageParam pageParam) {
        LambdaQueryWrapper<SysOperateLogDO> wrapper = new LambdaQueryWrapper<SysOperateLogDO>()
            .eq(TenantContext.getTenantId() != null, SysOperateLogDO::getTenantId, TenantContext.getTenantId())
            .like(module != null && !module.isBlank(), SysOperateLogDO::getModule, module)
            .like(name != null && !name.isBlank(), SysOperateLogDO::getName, name)
            .eq(result != null, SysOperateLogDO::getResult, result)
            .ge(startTime != null, SysOperateLogDO::getCreatedAt, startTime)
            .le(endTime != null, SysOperateLogDO::getCreatedAt, endTime)
            .orderByDesc(SysOperateLogDO::getId);

        Page<SysOperateLogDO> page = sysOperateLogMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    public int cleanExpiredLogs(LocalDateTime before) {
        LambdaQueryWrapper<SysOperateLogDO> wrapper = new LambdaQueryWrapper<SysOperateLogDO>()
            .lt(SysOperateLogDO::getCreatedAt, before);
        return sysOperateLogMapper.delete(wrapper);
    }
}
