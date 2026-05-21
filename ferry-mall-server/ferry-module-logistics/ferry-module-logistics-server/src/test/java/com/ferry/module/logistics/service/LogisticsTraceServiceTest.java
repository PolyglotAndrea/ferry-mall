package com.ferry.module.logistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.module.logistics.dal.dataobject.LogisticsTraceDO;
import com.ferry.module.logistics.dal.mapper.LogisticsTraceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticsTraceServiceTest {

    @Mock
    private LogisticsTraceMapper logisticsTraceMapper;

    @InjectMocks
    private LogisticsTraceService logisticsTraceService;

    @Test
    void trace_withRecords_returnsTrace() {
        LogisticsTraceDO record = new LogisticsTraceDO();
        record.setLogisticsNo("SF123456");
        record.setCompany("顺丰速运");
        record.setTraceContent("已到达上海转运中心");

        when(logisticsTraceMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of(record));

        var resp = logisticsTraceService.trace("SF123456");

        assertEquals("SF123456", resp.logisticsNo());
        assertEquals("顺丰速运", resp.company());
        assertEquals(1, resp.traces().size());
    }

    @Test
    void trace_noRecords_returnsDefault() {
        when(logisticsTraceMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of());

        var resp = logisticsTraceService.trace("SF999999");

        assertEquals("SF999999", resp.logisticsNo());
        assertEquals("Ferry Express", resp.company());
        assertEquals(List.of("暂无物流轨迹"), resp.traces());
    }

    @Test
    void createShipRecord_success() {
        var result = logisticsTraceService.createShipRecord(1L, "SF123456", "顺丰速运");

        assertTrue(result);
        verify(logisticsTraceMapper).insert(any(LogisticsTraceDO.class));
    }

    @Test
    void addTrace_success() {
        LogisticsTraceDO latest = new LogisticsTraceDO();
        latest.setLogisticsNo("SF123456");
        latest.setCompany("顺丰速运");
        latest.setOrderId(1L);

        when(logisticsTraceMapper.selectOne(any(LambdaQueryWrapper.class)))
            .thenReturn(latest);

        var result = logisticsTraceService.addTrace("SF123456", "已签收");

        assertTrue(result);
        verify(logisticsTraceMapper).insert(any(LogisticsTraceDO.class));
    }
}
