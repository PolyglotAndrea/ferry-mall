package com.ferry.module.order.controller.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.module.order.api.dto.OrderCreateReq;
import com.ferry.module.order.api.dto.OrderItemCreateReq;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.order.service.OrderTradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppOrderControllerTest {

    private MockMvc mockMvc;
    private OrderTradeService orderTradeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        orderTradeService = mock(OrderTradeService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AppOrderController(orderTradeService)).build();
    }

    @Test
    void create_returnsOrder() throws Exception {
        var resp = new OrderResp(
            1L, "FM202401010000001234", 10000, 0, 10000, 10, "待付款",
            "张三", "13800000000", "上海市", null,
            null, null, null, null, null,
            LocalDateTime.now(), List.of());

        when(orderTradeService.create(any())).thenReturn(resp);

        var req = new OrderCreateReq(
            List.of(new OrderItemCreateReq(1L, null, 1)),
            "张三", "13800000000", "上海市", null);

        mockMvc.perform(post("/app-api/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.orderNo").value("FM202401010000001234"));
    }
}
