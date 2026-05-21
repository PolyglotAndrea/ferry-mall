package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.member.dal.dataobject.CommissionRecordDO;
import com.ferry.module.member.dal.dataobject.CommissionUserDO;
import com.ferry.module.member.service.CommissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/member/commission")
public class AppCommissionController {

    private final CommissionService commissionService;

    public AppCommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @PostMapping("/apply")
    public CommonResult<Void> apply() {
        commissionService.apply(10001L);
        return CommonResult.success(null);
    }

    @GetMapping("/info")
    public CommonResult<CommissionUserDO> getPromoter() {
        return CommonResult.success(commissionService.getPromoter(10001L));
    }

    @GetMapping("/records")
    public CommonResult<PageResult<CommissionRecordDO>> getRecords(PageParam pageParam) {
        return CommonResult.success(commissionService.getRecords(10001L, pageParam));
    }

    @GetMapping("/team")
    public CommonResult<List<CommissionUserDO>> getTeam() {
        return CommonResult.success(commissionService.getTeam(10001L));
    }
}
