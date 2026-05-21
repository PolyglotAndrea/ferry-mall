package com.ferry.module.order.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.order.service.OrderTradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/order")
public class AdminOrderController {
    private final OrderTradeService orderTradeService;

    public AdminOrderController(OrderTradeService orderTradeService) {
        this.orderTradeService = orderTradeService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<OrderResp>> page(PageParam pageParam) {
        return CommonResult.success(orderTradeService.page(pageParam, null, null));
    }
}
