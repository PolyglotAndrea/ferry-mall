package com.ferry.module.member.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.member.dal.dataobject.MemberLevelDO;
import com.ferry.module.member.service.MemberLevelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-api/member/level")
public class AdminMemberLevelController {

    private final MemberLevelService memberLevelService;

    public AdminMemberLevelController(MemberLevelService memberLevelService) {
        this.memberLevelService = memberLevelService;
    }

    @GetMapping("/page")
    @RequirePermission("member:level:page")
    public CommonResult<PageResult<MemberLevelDO>> page(PageParam pageParam) {
        return CommonResult.success(memberLevelService.page(pageParam));
    }

    @GetMapping("/{id}")
    @RequirePermission("member:level:detail")
    public CommonResult<MemberLevelDO> detail(@PathVariable Long id) {
        return CommonResult.success(memberLevelService.detail(id));
    }

    @PostMapping("/create")
    @RequirePermission("member:level:create")
    public CommonResult<MemberLevelDO> create(@RequestParam String name,
                                               @RequestParam Integer minPoints,
                                               @RequestParam Integer discountPercent) {
        return CommonResult.success(memberLevelService.create(name, minPoints, discountPercent));
    }

    @PutMapping("/{id}/update")
    @RequirePermission("member:level:update")
    public CommonResult<MemberLevelDO> update(@PathVariable Long id,
                                               @RequestParam String name,
                                               @RequestParam Integer minPoints,
                                               @RequestParam Integer discountPercent) {
        return CommonResult.success(memberLevelService.update(id, name, minPoints, discountPercent));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("member:level:delete")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        memberLevelService.delete(id);
        return CommonResult.success(true);
    }
}
