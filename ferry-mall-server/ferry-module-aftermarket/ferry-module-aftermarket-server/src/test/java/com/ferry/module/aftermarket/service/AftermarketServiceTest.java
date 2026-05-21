package com.ferry.module.aftermarket.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.aftermarket.api.dto.AftermarketApplyReq;
import com.ferry.module.aftermarket.dal.dataobject.AftermarketRecordDO;
import com.ferry.module.aftermarket.dal.mapper.AftermarketRecordMapper;
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
class AftermarketServiceTest {

    @Mock
    private AftermarketRecordMapper aftermarketRecordMapper;

    @InjectMocks
    private AftermarketService aftermarketService;

    @Test
    void apply_success() {
        AftermarketApplyReq req = new AftermarketApplyReq(1L, "质量问题");

        var resp = aftermarketService.apply(req);

        assertNotNull(resp);
        verify(aftermarketRecordMapper).insert(any(AftermarketRecordDO.class));
    }

    @Test
    void approve_success() {
        AftermarketRecordDO record = new AftermarketRecordDO();
        record.setId(1L);
        record.setOrderId(1L);
        record.setStatus(10);

        when(aftermarketRecordMapper.selectById(1L)).thenReturn(record);

        var resp = aftermarketService.approve(1L);

        assertEquals(20, resp.status());
        assertEquals("处理中", resp.statusText());
    }

    @Test
    void approve_notFound_throws() {
        when(aftermarketRecordMapper.selectById(1L)).thenReturn(null);

        assertThrows(FerryBusinessException.class, () -> aftermarketService.approve(1L));
    }

    @Test
    void page_returnsPagedResult() {
        AftermarketRecordDO record = new AftermarketRecordDO();
        record.setId(1L);
        record.setOrderId(1L);
        record.setReason("质量问题");
        record.setStatus(10);

        Page<AftermarketRecordDO> page = new Page<>(1, 10);
        page.setRecords(List.of(record));
        page.setTotal(1);

        when(aftermarketRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        var result = aftermarketService.page(new PageParam(1, 10));

        assertEquals(1, result.list().size());
        assertEquals("待审核", result.list().get(0).statusText());
    }
}
