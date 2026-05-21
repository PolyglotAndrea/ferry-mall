package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.CartDO;
import com.ferry.module.member.service.CartService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-api/member/cart")
public class AppCartController {
    private final CartService cartService;

    public AppCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/list")
    public CommonResult<List<CartDO>> list() {
        return CommonResult.success(cartService.list(10001L));
    }

    @PostMapping("/add")
    public CommonResult<CartDO> add(@RequestParam Long spuId,
                                    @RequestParam(required = false) Long skuId,
                                    @RequestParam(defaultValue = "1") Integer quantity) {
        return CommonResult.success(cartService.add(10001L, spuId, skuId, quantity));
    }

    @PutMapping("/{cartId}/quantity")
    public CommonResult<Void> updateQuantity(@PathVariable Long cartId,
                                             @RequestParam Integer quantity) {
        cartService.updateQuantity(10001L, cartId, quantity);
        return CommonResult.success(null);
    }

    @DeleteMapping("/{cartId}")
    public CommonResult<Void> remove(@PathVariable Long cartId) {
        cartService.remove(10001L, cartId);
        return CommonResult.success(null);
    }

    @PostMapping("/{cartId}/select")
    public CommonResult<Void> toggleSelect(@PathVariable Long cartId,
                                           @RequestParam Integer selected) {
        cartService.toggleSelect(10001L, cartId, selected);
        return CommonResult.success(null);
    }

    @PostMapping("/clear")
    public CommonResult<Void> clear() {
        cartService.clear(10001L);
        return CommonResult.success(null);
    }

    @PostMapping("/select-all")
    public CommonResult<Void> selectAll(@RequestParam Integer selected) {
        cartService.toggleAllSelect(10001L, selected);
        return CommonResult.success(null);
    }

    @GetMapping("/count")
    public CommonResult<Integer> getCartCount() {
        return CommonResult.success(cartService.getCartCount(10001L));
    }
}
