package com.ferry.module.store.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
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

    public PageResult<LiveRoomDO> page(PageParam pageParam) {
        LambdaQueryWrapper<LiveRoomDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LiveRoomDO::getStatus, 1)
               .eq(LiveRoomDO::getTenantId, TenantContext.getTenantId())
               .orderByDesc(LiveRoomDO::getId);
        Page<LiveRoomDO> page = liveRoomMapper.selectPage(
                new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    public LiveRoomDO detail(Long id) {
        LiveRoomDO room = liveRoomMapper.selectById(id);
        if (room == null || room.getStatus() != 1) {
            throw new FerryBusinessException(404, "直播间不存在或已关闭");
        }
        return room;
    }
}
