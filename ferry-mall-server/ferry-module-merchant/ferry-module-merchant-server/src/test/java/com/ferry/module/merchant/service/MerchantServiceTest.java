package com.ferry.module.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.merchant.api.dto.MerchantApplyReq;
import com.ferry.module.merchant.dal.dataobject.MerchantInfoDO;
import com.ferry.module.merchant.dal.mapper.MerchantInfoMapper;
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
class MerchantServiceTest {

    @Mock
    private MerchantInfoMapper merchantInfoMapper;

    @InjectMocks
    private MerchantService merchantService;

    @Test
    void apply_success() {
        MerchantApplyReq req = new MerchantApplyReq("Ferry科技", "张三", "13800000000", "ABC123456");

        var resp = merchantService.apply(req);

        assertNotNull(resp);
        assertEquals("Ferry科技", resp.name());
        assertEquals(10, resp.status());
        verify(merchantInfoMapper).insert(any(MerchantInfoDO.class));
    }

    @Test
    void approve_success() {
        MerchantInfoDO merchant = new MerchantInfoDO();
        merchant.setId(1L);
        merchant.setName("Ferry科技");
        merchant.setStatus(10);

        when(merchantInfoMapper.selectById(1L)).thenReturn(merchant);

        var resp = merchantService.approve(1L);

        assertEquals(20, resp.status());
        assertEquals("已通过", resp.statusText());
    }

    @Test
    void reject_success() {
        MerchantInfoDO merchant = new MerchantInfoDO();
        merchant.setId(1L);
        merchant.setName("Ferry科技");
        merchant.setStatus(10);

        when(merchantInfoMapper.selectById(1L)).thenReturn(merchant);

        var resp = merchantService.reject(1L);

        assertEquals(30, resp.status());
        assertEquals("已拒绝", resp.statusText());
    }

    @Test
    void detail_notFound_throws() {
        when(merchantInfoMapper.selectById(1L)).thenReturn(null);

        assertThrows(FerryBusinessException.class, () -> merchantService.detail(1L));
    }

    @Test
    void page_returnsPagedResult() {
        MerchantInfoDO merchant = new MerchantInfoDO();
        merchant.setId(1L);
        merchant.setName("Ferry科技");
        merchant.setStatus(20);

        Page<MerchantInfoDO> page = new Page<>(1, 10);
        page.setRecords(List.of(merchant));
        page.setTotal(1);

        when(merchantInfoMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        var result = merchantService.page(new PageParam(1, 10));

        assertEquals(1, result.list().size());
        assertEquals("已通过", result.list().get(0).statusText());
    }
}
