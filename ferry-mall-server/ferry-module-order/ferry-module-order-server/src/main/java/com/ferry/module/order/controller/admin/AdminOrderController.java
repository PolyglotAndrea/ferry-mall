package com.ferry.module.order.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.order.api.dto.OrderShipReq;
import com.ferry.module.order.service.OrderTradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/order")
public class AdminOrderController {
    private final OrderTradeService orderTradeService;

    public AdminOrderController(OrderTradeService orderTradeService) {
        this.orderTradeService = orderTradeService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<OrderResp>> page(
            PageParam pageParam,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(orderTradeService.adminPage(pageParam, status, keyword));
    }

    @GetMapping("/{orderNo}")
    public CommonResult<OrderResp> detail(@PathVariable String orderNo) {
        return CommonResult.success(orderTradeService.detail(orderNo));
    }

    @PostMapping("/{orderNo}/deliver")
    public CommonResult<OrderResp> deliver(
            @PathVariable String orderNo,
            @Valid @RequestBody OrderShipReq req) {
        return CommonResult.success(orderTradeService.deliver(orderNo, req.logisticsCompany(), req.logisticsNo()));
    }
}
