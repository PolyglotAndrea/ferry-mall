package com.ferry.module.system.service;

import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.system.dal.dataobject.SysOperateLogDO;
import com.ferry.module.system.dal.mapper.SysOperateLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
