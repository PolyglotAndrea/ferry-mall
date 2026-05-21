package com.ferry.module.settlement.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.settlement.api.dto.SettlementBillResp;
import com.ferry.module.settlement.service.SettlementBillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/settlement")
public class AdminSettlementController {
    private final SettlementBillService settlementBillService;

    public AdminSettlementController(SettlementBillService settlementBillService) {
        this.settlementBillService = settlementBillService;
    }

    @GetMapping("/bill/page")
    public CommonResult<PageResult<SettlementBillResp>> page(PageParam pageParam) {
        return CommonResult.success(settlementBillService.page(pageParam));
    }

    @PostMapping("/bill/{id}/settle")
    public CommonResult<SettlementBillResp> settle(@PathVariable Long id) {
        return CommonResult.success(settlementBillService.settle(id));
    }
}
