package com.ferry.module.store.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.store.dal.dataobject.LiveRoomDO;
import com.ferry.module.store.service.LiveRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/store/live")
public class AppLiveRoomController {

    private final LiveRoomService liveRoomService;

    public AppLiveRoomController(LiveRoomService liveRoomService) {
        this.liveRoomService = liveRoomService;
    }

    @GetMapping("/list")
    public CommonResult<List<LiveRoomDO>> list() {
        return CommonResult.success(liveRoomService.list());
    }

    @GetMapping("/page")
    public CommonResult<PageResult<LiveRoomDO>> page(PageParam pageParam) {
        return CommonResult.success(liveRoomService.page(pageParam));
    }

    @GetMapping("/{id}")
    public CommonResult<LiveRoomDO> detail(@PathVariable Long id) {
        return CommonResult.success(liveRoomService.detail(id));
    }
}
