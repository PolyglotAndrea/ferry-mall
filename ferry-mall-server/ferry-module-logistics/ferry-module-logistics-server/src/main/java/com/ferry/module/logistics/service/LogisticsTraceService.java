package com.ferry.module.logistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.module.logistics.api.dto.LogisticsTraceResp;
import com.ferry.module.logistics.dal.dataobject.LogisticsTraceDO;
import com.ferry.module.logistics.dal.mapper.LogisticsTraceMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogisticsTraceService {
    private final LogisticsTraceMapper logisticsTraceMapper;

    public LogisticsTraceService(LogisticsTraceMapper logisticsTraceMapper) {
        this.logisticsTraceMapper = logisticsTraceMapper;
    }

    public LogisticsTraceResp trace(String logisticsNo) {
        List<LogisticsTraceDO> records = logisticsTraceMapper.selectList(new LambdaQueryWrapper<LogisticsTraceDO>()
            .eq(LogisticsTraceDO::getLogisticsNo, logisticsNo)
            .orderByAsc(LogisticsTraceDO::getId));
        if (records.isEmpty()) {
            return new LogisticsTraceResp(logisticsNo, "Ferry Express", List.of("暂无物流轨迹"));
        }
        return new LogisticsTraceResp(logisticsNo, records.get(0).getCompany(), records.stream().map(LogisticsTraceDO::getTraceContent).toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean createShipRecord(Long orderId, String logisticsNo, String company) {
        LogisticsTraceDO record = new LogisticsTraceDO();
        record.setOrderId(orderId);
        record.setLogisticsNo(logisticsNo);
        record.setCompany(company);
        record.setTraceContent("已发货");
        logisticsTraceMapper.insert(record);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean addTrace(String logisticsNo, String content) {
        LogisticsTraceDO latest = logisticsTraceMapper.selectOne(
            new LambdaQueryWrapper<LogisticsTraceDO>()
                .eq(LogisticsTraceDO::getLogisticsNo, logisticsNo)
                .orderByDesc(LogisticsTraceDO::getId)
                .last("limit 1"));
        String company = latest != null ? latest.getCompany() : "Ferry Express";
        Long orderId = latest != null ? latest.getOrderId() : 0L;
        LogisticsTraceDO record = new LogisticsTraceDO();
        record.setOrderId(orderId);
        record.setLogisticsNo(logisticsNo);
        record.setCompany(company);
        record.setTraceContent(content);
        logisticsTraceMapper.insert(record);
        return true;
    }
}
