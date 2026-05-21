package com.ferry.module.payment.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.payment.api.dto.RefundCreateReq;
import com.ferry.module.payment.api.dto.RefundResp;
import com.ferry.module.payment.dal.dataobject.PaymentChannelDO;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.service.PaymentChannelService;
import com.ferry.module.payment.service.PaymentRecordService;
import com.ferry.module.payment.service.PaymentRefundService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/payment")
public class AdminPaymentController {

    private final PaymentRefundService paymentRefundService;
    private final PaymentChannelService paymentChannelService;
    private final PaymentRecordService paymentRecordService;

    public AdminPaymentController(PaymentRefundService paymentRefundService,
                                  PaymentChannelService paymentChannelService,
                                  PaymentRecordService paymentRecordService) {
        this.paymentRefundService = paymentRefundService;
        this.paymentChannelService = paymentChannelService;
        this.paymentRecordService = paymentRecordService;
    }

    // ========== 退款管理 ==========

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

    // ========== 支付渠道管理 ==========

    @GetMapping("/channel/page")
    @RequirePermission("payment:channel:page")
    public CommonResult<PageResult<PaymentChannelDO>> channelPage(PageParam pageParam) {
        return CommonResult.success(paymentChannelService.page(pageParam));
    }

    @GetMapping("/channel/{id}")
    @RequirePermission("payment:channel:detail")
    public CommonResult<PaymentChannelDO> channelDetail(@PathVariable Long id) {
        return CommonResult.success(paymentChannelService.detail(id));
    }

    @PostMapping("/channel/create")
    @RequirePermission("payment:channel:create")
    public CommonResult<PaymentChannelDO> createChannel(@RequestBody PaymentChannelDO channel) {
        return CommonResult.success(paymentChannelService.create(channel));
    }

    @PutMapping("/channel/{id}")
    @RequirePermission("payment:channel:update")
    public CommonResult<PaymentChannelDO> updateChannel(@PathVariable Long id, @RequestBody PaymentChannelDO channel) {
        return CommonResult.success(paymentChannelService.update(id, channel));
    }

    @DeleteMapping("/channel/{id}")
    @RequirePermission("payment:channel:delete")
    public CommonResult<Void> deleteChannel(@PathVariable Long id) {
        paymentChannelService.delete(id);
        return CommonResult.success(null);
    }

    @PutMapping("/channel/{id}/toggle")
    @RequirePermission("payment:channel:update")
    public CommonResult<PaymentChannelDO> toggleChannel(@PathVariable Long id) {
        return CommonResult.success(paymentChannelService.toggleEnabled(id));
    }

    // ========== 支付记录查询 ==========

    @GetMapping("/record/page")
    @RequirePermission("payment:record:page")
    public CommonResult<PageResult<PaymentRecordDO>> recordPage(
            PageParam pageParam,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Integer status) {
        return CommonResult.success(paymentRecordService.page(pageParam, orderNo, channel, status));
    }

    @GetMapping("/record/{id}")
    @RequirePermission("payment:record:detail")
    public CommonResult<PaymentRecordDO> recordDetail(@PathVariable Long id) {
        return CommonResult.success(paymentRecordService.detail(id));
    }
}
