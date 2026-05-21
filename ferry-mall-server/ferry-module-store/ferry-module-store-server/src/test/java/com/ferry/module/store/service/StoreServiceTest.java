package com.ferry.module.store.service;

import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.store.dal.dataobject.StoreInfoDO;
import com.ferry.module.store.dal.mapper.StoreInfoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreInfoMapper storeInfoMapper;

    @InjectMocks
    private StoreService storeService;

    @Test
    void create_success() {
        var resp = storeService.create(1L, "Ferry旗舰店", "https://logo.png", "品质优选");

        assertNotNull(resp);
        assertEquals("Ferry旗舰店", resp.name());
        assertEquals(1, resp.status());
        assertEquals(5.0, resp.score());
        verify(storeInfoMapper).insert(any(StoreInfoDO.class));
    }

    @Test
    void update_success() {
        StoreInfoDO store = new StoreInfoDO();
        store.setId(1L);
        store.setName("Ferry旗舰店");
        store.setStatus(1);

        when(storeInfoMapper.selectById(1L)).thenReturn(store);

        var resp = storeService.update(1L, "Ferry旗舰店新版", "https://new-logo.png", "全新升级");

        assertEquals("Ferry旗舰店新版", resp.name());
        verify(storeInfoMapper).updateById(store);
    }

    @Test
    void detail_notFound_throws() {
        when(storeInfoMapper.selectById(1L)).thenReturn(null);

        assertThrows(FerryBusinessException.class, () -> storeService.detail(1L));
    }
}
