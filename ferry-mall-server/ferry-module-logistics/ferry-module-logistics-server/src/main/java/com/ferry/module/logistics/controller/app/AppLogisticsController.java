package com.ferry.module.logistics.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.logistics.api.dto.LogisticsTraceResp;
import com.ferry.module.logistics.service.LogisticsTraceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/logistics")
public class AppLogisticsController {
    private final LogisticsTraceService logisticsTraceService;

    public AppLogisticsController(LogisticsTraceService logisticsTraceService) {
        this.logisticsTraceService = logisticsTraceService;
    }

    @GetMapping("/trace")
    public CommonResult<LogisticsTraceResp> trace(@RequestParam String logisticsNo) {
        return CommonResult.success(logisticsTraceService.trace(logisticsNo));
    }
}
