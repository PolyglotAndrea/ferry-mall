package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.MemberFootprintDO;
import com.ferry.module.member.service.MemberFootprintService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/member/footprint")
public class AppMemberFootprintController {
    private final MemberFootprintService memberFootprintService;

    public AppMemberFootprintController(MemberFootprintService memberFootprintService) {
        this.memberFootprintService = memberFootprintService;
    }

    @GetMapping("/list")
    public CommonResult<List<MemberFootprintDO>> list() {
        return CommonResult.success(memberFootprintService.list(10001L));
    }

    @PostMapping("/add")
    public CommonResult<MemberFootprintDO> add(@RequestParam Long spuId,
                                               @RequestParam String spuName,
                                               @RequestParam String spuCover,
                                               @RequestParam Integer priceCent) {
        return CommonResult.success(memberFootprintService.add(10001L, spuId, spuName, spuCover, priceCent));
    }
}
