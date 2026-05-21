package com.ferry.module.marketing.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.marketing.dal.dataobject.BargainActivityDO;
import com.ferry.module.marketing.dal.dataobject.BargainRecordDO;
import com.ferry.module.marketing.service.BargainService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/marketing/bargain")
public class AppBargainController {
    private final BargainService bargainService;

    public AppBargainController(BargainService bargainService) {
        this.bargainService = bargainService;
    }

    @GetMapping("/activities")
    public CommonResult<List<BargainActivityDO>> listActivities() {
        return CommonResult.success(bargainService.listActivities());
    }

    @PostMapping("/{activityId}/start")
    public CommonResult<BargainRecordDO> startBargain(@PathVariable Long activityId) {
        Long memberId = 10001L;
        return CommonResult.success(bargainService.startBargain(activityId, memberId));
    }

    @PostMapping("/{recordId}/help")
    public CommonResult<BargainRecordDO> helpBargain(@PathVariable Long recordId) {
        Long memberId = 10001L;
        return CommonResult.success(bargainService.helpBargain(recordId, memberId));
    }

    @GetMapping("/record/{recordId}")
    public CommonResult<BargainRecordDO> getRecord(@PathVariable Long recordId) {
        return CommonResult.success(bargainService.getRecord(recordId));
    }
}
