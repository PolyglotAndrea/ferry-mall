package com.ferry.module.system.service;

import com.ferry.module.system.api.dto.MenuNodeResp;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminMenuService {
    public List<MenuNodeResp> tree() {
        return List.of(
            new MenuNodeResp("控制台", "/dashboard", "DataBoard", List.of()),
            new MenuNodeResp("商品中心", "/product", "Goods", List.of(
                new MenuNodeResp("商品列表", "/product/spu", "GoodsFilled", List.of()),
                new MenuNodeResp("商品分类", "/product/category", "Menu", List.of())
            )),
            new MenuNodeResp("订单中心", "/order", "Tickets", List.of(
                new MenuNodeResp("订单列表", "/order/list", "List", List.of())
            )),
            new MenuNodeResp("会员中心", "/member", "User", List.of(
                new MenuNodeResp("会员列表", "/member/list", "UserFilled", List.of())
            ))
        );
    }
}
