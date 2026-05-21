package com.ferry.module.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.settlement.dal.dataobject.SettlementBillDO;
import com.ferry.module.settlement.dal.mapper.SettlementBillMapper;
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
class SettlementBillServiceTest {

    @Mock
    private SettlementBillMapper settlementBillMapper;

    @InjectMocks
    private SettlementBillService settlementBillService;

    @Test
    void createBill_success() {
        var resp = settlementBillService.createBill(1L, "Ferry科技", 100000, 5000);

        assertNotNull(resp);
        assertEquals("Ferry科技", resp.merchantName());
        assertEquals(100000, resp.orderAmountCent());
        assertEquals(5000, resp.commissionCent());
        assertEquals(95000, resp.payableCent());
        assertEquals(10, resp.status());
        verify(settlementBillMapper).insert(any(SettlementBillDO.class));
    }

    @Test
    void settle_success() {
        SettlementBillDO bill = new SettlementBillDO();
        bill.setId(1L);
        bill.setMerchantId(1L);
        bill.setStatus(10);

        when(settlementBillMapper.selectById(1L)).thenReturn(bill);

        var resp = settlementBillService.settle(1L);

        assertEquals(20, resp.status());
        assertEquals("已结算", resp.statusText());
    }

    @Test
    void settle_notFound_throws() {
        when(settlementBillMapper.selectById(1L)).thenReturn(null);

        assertThrows(FerryBusinessException.class, () -> settlementBillService.settle(1L));
    }

    @Test
    void page_returnsPagedResult() {
        SettlementBillDO bill = new SettlementBillDO();
        bill.setId(1L);
        bill.setMerchantId(1L);
        bill.setStatus(10);

        Page<SettlementBillDO> page = new Page<>(1, 10);
        page.setRecords(List.of(bill));
        page.setTotal(1);

        when(settlementBillMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        var result = settlementBillService.page(new PageParam(1, 10));

        assertEquals(1, result.list().size());
        assertEquals("待结算", result.list().get(0).statusText());
    }
}
