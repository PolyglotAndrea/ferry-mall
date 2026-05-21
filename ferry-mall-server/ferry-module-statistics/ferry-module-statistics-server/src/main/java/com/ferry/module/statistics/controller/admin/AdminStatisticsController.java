package com.ferry.module.statistics.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.statistics.api.dto.OverviewResp;
import com.ferry.module.statistics.api.dto.PendingCountResp;
import com.ferry.module.statistics.api.dto.ProductRankResp;
import com.ferry.module.statistics.service.StatisticsOverviewService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/statistics")
public class AdminStatisticsController {
    private final StatisticsOverviewService statisticsOverviewService;

    public AdminStatisticsController(StatisticsOverviewService statisticsOverviewService) {
        this.statisticsOverviewService = statisticsOverviewService;
    }

    @GetMapping("/overview")
    public CommonResult<OverviewResp> overview() {
        return CommonResult.success(statisticsOverviewService.overview());
    }

    @GetMapping("/top-products")
    public CommonResult<List<ProductRankResp>> topProducts(@RequestParam(defaultValue = "10") int limit) {
        return CommonResult.success(statisticsOverviewService.topProducts(limit));
    }

    @GetMapping("/daily-sales")
    public CommonResult<List<Map<String, Object>>> dailySales(@RequestParam(defaultValue = "7") int days) {
        return CommonResult.success(statisticsOverviewService.dailySales(days));
    }

    @GetMapping("/pending-count")
    public CommonResult<PendingCountResp> pendingCount() {
        return CommonResult.success(statisticsOverviewService.pendingCount());
    }
}
