package com.ferry.module.store.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.store.dal.dataobject.LiveRoomDO;
import com.ferry.module.store.dal.mapper.LiveRoomMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveRoomService {

    private final LiveRoomMapper liveRoomMapper;

    public LiveRoomService(LiveRoomMapper liveRoomMapper) {
        this.liveRoomMapper = liveRoomMapper;
    }

    public List<LiveRoomDO> list() {
        return liveRoomMapper.selectList(
            new LambdaQueryWrapper<LiveRoomDO>()
                .eq(LiveRoomDO::getStatus, 1)
                .eq(LiveRoomDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(LiveRoomDO::getId));
    }
}
