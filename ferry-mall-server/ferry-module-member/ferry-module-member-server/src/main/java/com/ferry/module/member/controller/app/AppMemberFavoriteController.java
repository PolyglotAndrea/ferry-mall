package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.MemberFavoriteDO;
import com.ferry.module.member.service.MemberFavoriteService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/member/favorite")
public class AppMemberFavoriteController {
    private final MemberFavoriteService memberFavoriteService;

    public AppMemberFavoriteController(MemberFavoriteService memberFavoriteService) {
        this.memberFavoriteService = memberFavoriteService;
    }

    @GetMapping("/list")
    public CommonResult<List<MemberFavoriteDO>> list() {
        return CommonResult.success(memberFavoriteService.list(10001L));
    }

    @PostMapping("/add")
    public CommonResult<MemberFavoriteDO> add(@RequestParam Long spuId,
                                              @RequestParam String spuName,
                                              @RequestParam String spuCover,
                                              @RequestParam Integer priceCent) {
        return CommonResult.success(memberFavoriteService.add(10001L, spuId, spuName, spuCover, priceCent));
    }

    @DeleteMapping("/{spuId}")
    public CommonResult<Void> remove(@PathVariable Long spuId) {
        memberFavoriteService.remove(10001L, spuId);
        return CommonResult.success(null);
    }

    @GetMapping("/exists")
    public CommonResult<Boolean> exists(@RequestParam Long spuId) {
        return CommonResult.success(memberFavoriteService.exists(10001L, spuId));
    }
}
