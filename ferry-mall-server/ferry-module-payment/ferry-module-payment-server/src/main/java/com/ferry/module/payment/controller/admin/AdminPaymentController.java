package com.ferry.module.payment.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.payment.api.dto.RefundCreateReq;
import com.ferry.module.payment.api.dto.RefundResp;
import com.ferry.module.payment.service.PaymentRefundService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/payment")
public class AdminPaymentController {

    private final PaymentRefundService paymentRefundService;

    public AdminPaymentController(PaymentRefundService paymentRefundService) {
        this.paymentRefundService = paymentRefundService;
    }

    @PostMapping("/refund/create")
    @RequirePermission("payment:refund:create")
    public CommonResult<RefundResp> createRefund(@Valid @RequestBody RefundCreateReq req) {
        return CommonResult.success(paymentRefundService.create(req));
    }

    @GetMapping("/refund/page")
    @RequirePermission("payment:refund:page")
    public CommonResult<PageResult<RefundResp>> refundPage(PageParam pageParam) {
        return CommonResult.success(paymentRefundService.page(pageParam));
    }

    @GetMapping("/refund/{id}")
    @RequirePermission("payment:refund:detail")
    public CommonResult<RefundResp> refundDetail(@PathVariable Long id) {
        return CommonResult.success(paymentRefundService.detail(id));
    }

    @PutMapping("/refund/{id}/approve")
    @RequirePermission("payment:refund:approve")
    public CommonResult<RefundResp> approveRefund(@PathVariable Long id) {
        return CommonResult.success(paymentRefundService.approve(id));
    }

    @PutMapping("/refund/{id}/reject")
    @RequirePermission("payment:refund:reject")
    public CommonResult<RefundResp> rejectRefund(@PathVariable Long id) {
        return CommonResult.success(paymentRefundService.reject(id));
    }
}
