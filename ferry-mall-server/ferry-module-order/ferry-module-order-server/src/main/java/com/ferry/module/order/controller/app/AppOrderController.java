package com.ferry.module.order.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.order.api.dto.OrderCancelReq;
import com.ferry.module.order.api.dto.OrderCreateReq;
import com.ferry.module.order.api.dto.OrderPayReq;
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
@RequestMapping("/app-api/order")
public class AppOrderController {
    private final OrderTradeService orderTradeService;

    public AppOrderController(OrderTradeService orderTradeService) {
        this.orderTradeService = orderTradeService;
    }

    @PostMapping("/create")
    public CommonResult<OrderResp> create(@Valid @RequestBody OrderCreateReq req) {
        return CommonResult.success(orderTradeService.create(req));
    }

    @PostMapping("/pay")
    public CommonResult<OrderResp> pay(@Valid @RequestBody OrderPayReq req) {
        return CommonResult.success(orderTradeService.pay(req));
    }

    @PostMapping("/ship")
    public CommonResult<OrderResp> ship(@Valid @RequestBody OrderShipReq req) {
        return CommonResult.success(orderTradeService.ship(req));
    }

    @PostMapping("/receive/{orderNo}")
    public CommonResult<OrderResp> receive(@PathVariable String orderNo) {
        return CommonResult.success(orderTradeService.receive(orderNo));
    }

    @PostMapping("/cancel")
    public CommonResult<OrderResp> cancel(@Valid @RequestBody OrderCancelReq req) {
        return CommonResult.success(orderTradeService.cancel(req));
    }

    @GetMapping("/{orderNo}")
    public CommonResult<OrderResp> detail(@PathVariable String orderNo) {
        return CommonResult.success(orderTradeService.detail(orderNo));
    }

    @GetMapping("/page")
    public CommonResult<PageResult<OrderResp>> page(PageParam pageParam,
                                                     @RequestParam(required = false) Integer status,
                                                     @RequestParam(required = false) String keyword) {
        return CommonResult.success(orderTradeService.page(pageParam, status, keyword));
    }
}
