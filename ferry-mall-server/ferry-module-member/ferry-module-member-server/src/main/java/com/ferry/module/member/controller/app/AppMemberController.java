package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.member.api.dto.MemberLoginReq;
import com.ferry.module.member.api.dto.MemberLoginResp;
import com.ferry.module.member.api.dto.MemberProfileResp;
import com.ferry.module.member.dal.dataobject.MemberIntegralRecordDO;
import com.ferry.module.member.service.MemberAuthService;
import com.ferry.module.member.service.MemberIntegralService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/member")
public class AppMemberController {
    private final MemberAuthService memberAuthService;
    private final MemberIntegralService memberIntegralService;

    public AppMemberController(MemberAuthService memberAuthService,
                               MemberIntegralService memberIntegralService) {
        this.memberAuthService = memberAuthService;
        this.memberIntegralService = memberIntegralService;
    }

    @PostMapping("/auth/login")
    public CommonResult<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req) {
        return CommonResult.success(memberAuthService.login(req));
    }

    @GetMapping("/profile")
    public CommonResult<MemberProfileResp> profile() {
        return CommonResult.success(memberAuthService.profile());
    }

    @PostMapping("/sign")
    public CommonResult<Integer> sign() {
        return CommonResult.success(memberIntegralService.sign(10001L));
    }

    @GetMapping("/integral/records")
    public CommonResult<PageResult<MemberIntegralRecordDO>> integralRecords(PageParam pageParam) {
        return CommonResult.success(memberIntegralService.records(10001L, pageParam));
    }
}
