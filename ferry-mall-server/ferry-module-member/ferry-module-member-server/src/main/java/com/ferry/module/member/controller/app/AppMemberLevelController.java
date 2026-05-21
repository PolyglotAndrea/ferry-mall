package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.MemberLevelDO;
import com.ferry.module.member.service.MemberLevelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/member/level")
public class AppMemberLevelController {

    private final MemberLevelService memberLevelService;

    public AppMemberLevelController(MemberLevelService memberLevelService) {
        this.memberLevelService = memberLevelService;
    }

    @GetMapping("/list")
    public CommonResult<List<MemberLevelDO>> list() {
        return CommonResult.success(memberLevelService.list());
    }

    @GetMapping("/current")
    public CommonResult<MemberLevelDO> current() {
        Long memberId = 10001L;
        return CommonResult.success(memberLevelService.resolveLevel(memberId));
    }
}
