package com.ferry.module.logistics.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.logistics.api.dto.LogisticsTraceResp;
import com.ferry.module.logistics.service.LogisticsTraceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/logistics")
public class AdminLogisticsController {
    private final LogisticsTraceService logisticsTraceService;

    public AdminLogisticsController(LogisticsTraceService logisticsTraceService) {
        this.logisticsTraceService = logisticsTraceService;
    }

    @GetMapping("/trace")
    public CommonResult<LogisticsTraceResp> trace(@RequestParam String logisticsNo) {
        return CommonResult.success(logisticsTraceService.trace(logisticsNo));
    }

    @PostMapping("/ship")
    public CommonResult<Boolean> ship(@RequestParam Long orderId,
                                       @RequestParam String logisticsNo,
                                       @RequestParam String company) {
        return CommonResult.success(logisticsTraceService.createShipRecord(orderId, logisticsNo, company));
    }
}
