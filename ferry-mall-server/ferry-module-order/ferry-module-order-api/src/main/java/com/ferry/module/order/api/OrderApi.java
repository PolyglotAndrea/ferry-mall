package com.ferry.module.order.api;

import com.ferry.module.order.api.dto.OrderResp;

public interface OrderApi {
    OrderResp detail(String orderNo);
    OrderResp pay(String orderNo);
}
