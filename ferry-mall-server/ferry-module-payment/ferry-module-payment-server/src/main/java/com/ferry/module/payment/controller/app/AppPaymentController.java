package com.ferry.module.payment.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.governance.Degrade;
import com.ferry.framework.web.governance.RateLimit;
import com.ferry.module.payment.api.dto.PaymentCallbackReq;
import com.ferry.module.payment.api.dto.PaymentPrepareReq;
import com.ferry.module.payment.api.dto.PaymentPrepareResp;
import com.ferry.module.payment.service.PaymentCallbackService;
import com.ferry.module.payment.service.PaymentPrepareService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/payment")
public class AppPaymentController {
    private final PaymentPrepareService paymentPrepareService;
    private final PaymentCallbackService paymentCallbackService;

    public AppPaymentController(PaymentPrepareService paymentPrepareService,
                                PaymentCallbackService paymentCallbackService) {
        this.paymentPrepareService = paymentPrepareService;
        this.paymentCallbackService = paymentCallbackService;
    }

    @RateLimit(key = "payment:prepare", permitsPerSecond = 20)
    @Degrade(message = "支付服务繁忙，请稍后重试")
    @PostMapping("/prepare")
    public CommonResult<PaymentPrepareResp> prepare(@Valid @RequestBody PaymentPrepareReq req) {
        return CommonResult.success(paymentPrepareService.prepare(req));
    }

    @PostMapping("/callback")
    public CommonResult<Void> callback(@Valid @RequestBody PaymentCallbackReq req) {
        paymentCallbackService.callback(req);
        return CommonResult.success(null);
    }
}
