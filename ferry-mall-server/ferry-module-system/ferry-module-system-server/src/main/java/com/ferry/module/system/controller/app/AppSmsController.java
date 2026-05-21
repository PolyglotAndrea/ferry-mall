package com.ferry.module.system.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.service.SmsService;
import com.ferry.module.system.service.VerifyCodeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/sms")
public class AppSmsController {

    private final SmsService smsService;
    private final VerifyCodeService verifyCodeService;

    public AppSmsController(SmsService smsService, VerifyCodeService verifyCodeService) {
        this.smsService = smsService;
        this.verifyCodeService = verifyCodeService;
    }

    @PostMapping("/send-verify-code")
    public CommonResult<Boolean> sendVerifyCode(
            @RequestParam @NotBlank @Pattern(regexp = "^1[3-9]\\d{9}$") String mobile) {
        String code = verifyCodeService.generate(mobile);
        smsService.sendVerifyCode(mobile, code);
        return CommonResult.success(true);
    }

    @PostMapping("/verify-code")
    public CommonResult<Boolean> verifyCode(
            @RequestParam @NotBlank String mobile,
            @RequestParam @NotBlank String code) {
        boolean valid = verifyCodeService.verify(mobile, code);
        if (!valid) {
            throw new FerryBusinessException(400, "验证码错误或已过期");
        }
        return CommonResult.success(true);
    }
}
