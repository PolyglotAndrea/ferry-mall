package com.ferry.module.marketing.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.marketing.dal.dataobject.GrouponActivityDO;
import com.ferry.module.marketing.service.GrouponService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/marketing/groupon")
public class AppGrouponController {
    private final GrouponService grouponService;

    public AppGrouponController(GrouponService grouponService) {
        this.grouponService = grouponService;
    }

    @GetMapping("/activities")
    public CommonResult<List<GrouponActivityDO>> listActivities() {
        return CommonResult.success(grouponService.listActivities());
    }

    @GetMapping("/{activityId}")
    public CommonResult<GrouponActivityDO> getActivityDetail(@PathVariable Long activityId) {
        return CommonResult.success(grouponService.getActivityDetail(activityId));
    }

    @PostMapping("/{activityId}/join")
    public CommonResult<Boolean> join(@PathVariable Long activityId) {
        Long memberId = 10001L;
        return CommonResult.success(grouponService.join(activityId, memberId));
    }
}
