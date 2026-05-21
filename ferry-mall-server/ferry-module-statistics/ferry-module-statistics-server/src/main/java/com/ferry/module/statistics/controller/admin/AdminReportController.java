package com.ferry.module.statistics.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.statistics.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-api/report")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/trade-overview")
    @RequirePermission("report:trade:overview")
    public CommonResult<Map<String, Object>> tradeOverview() {
        return CommonResult.success(reportService.tradeOverview());
    }

    @GetMapping("/product-sales-rank")
    @RequirePermission("report:product:sales")
    public CommonResult<List<Map<String, Object>>> productSalesRank(@RequestParam(defaultValue = "10") int limit) {
        return CommonResult.success(reportService.productSalesRank(limit));
    }

    @GetMapping("/member-growth")
    @RequirePermission("report:member:growth")
    public CommonResult<List<Map<String, Object>>> memberGrowth(@RequestParam(defaultValue = "30") int days) {
        return CommonResult.success(reportService.memberGrowth(days));
    }

    @GetMapping("/hourly-order-distribution")
    @RequirePermission("report:order:hourly")
    public CommonResult<List<Map<String, Object>>> hourlyOrderDistribution() {
        return CommonResult.success(reportService.hourlyOrderDistribution());
    }
}
