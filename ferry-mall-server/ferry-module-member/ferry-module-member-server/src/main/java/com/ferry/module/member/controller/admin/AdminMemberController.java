package com.ferry.module.member.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.member.dal.dataobject.MemberIntegralRecordDO;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.service.MemberIntegralService;
import com.ferry.module.member.service.MemberLevelService;
import com.ferry.module.member.service.MemberUserAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-api/member")
public class AdminMemberController {

    private final MemberUserAdminService memberUserAdminService;
    private final MemberIntegralService memberIntegralService;
    private final MemberLevelService memberLevelService;

    public AdminMemberController(MemberUserAdminService memberUserAdminService,
                                  MemberIntegralService memberIntegralService,
                                  MemberLevelService memberLevelService) {
        this.memberUserAdminService = memberUserAdminService;
        this.memberIntegralService = memberIntegralService;
        this.memberLevelService = memberLevelService;
    }

    @GetMapping("/page")
    @RequirePermission("member:user:page")
    public CommonResult<PageResult<MemberUserAdminService.MemberUserPageItem>> page(
            @RequestParam(required = false) String keyword,
            PageParam pageParam) {
        return CommonResult.success(memberUserAdminService.page(keyword, pageParam));
    }

    @GetMapping("/{id}")
    @RequirePermission("member:user:detail")
    public CommonResult<MemberUserAdminService.MemberUserDetailResp> detail(@PathVariable Long id) {
        MemberUserDO user = memberUserAdminService.detail(id);
        if (user == null) {
            throw new FerryBusinessException(404, "会员不存在");
        }

        MemberUserAdminService.MemberUserDetailResp resp = new MemberUserAdminService.MemberUserDetailResp();
        resp.setId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatarUrl());
        resp.setMobile(user.getMobile());
        resp.setPoints(user.getPoints());
        resp.setStatus(user.getStatus());

        var stats = memberUserAdminService.stats(id);
        resp.setOrderCount(stats.getOrderCount());
        resp.setTotalSpendCent(stats.getTotalSpendCent());

        var level = memberLevelService.resolveLevel(id);
        if (level != null) {
            resp.setLevel(level.getName());
            resp.setLevelId(level.getId());
        }

        return CommonResult.success(resp);
    }

    @GetMapping("/{id}/integral-records")
    @RequirePermission("member:user:integral")
    public CommonResult<PageResult<MemberIntegralRecordDO>> integralRecords(
            @PathVariable Long id,
            PageParam pageParam) {
        return CommonResult.success(memberIntegralService.records(id, pageParam));
    }
}
