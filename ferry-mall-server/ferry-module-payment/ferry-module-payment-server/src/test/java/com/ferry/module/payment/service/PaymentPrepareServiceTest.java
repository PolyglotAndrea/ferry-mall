package com.ferry.module.payment.service;

import com.ferry.module.order.api.OrderApi;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.payment.api.dto.PaymentPrepareReq;
import com.ferry.module.payment.dal.dataobject.PaymentChannelDO;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentChannelMapper;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentPrepareServiceTest {

    @Mock
    private PaymentChannelMapper paymentChannelMapper;

    @Mock
    private PaymentRecordMapper paymentRecordMapper;

    @Mock
    private OrderApi orderApi;

    @InjectMocks
    private PaymentPrepareService paymentPrepareService;

    @Test
    void prepare_success() {
        OrderResp order = new OrderResp(
            1L, "FM202401010000001234", 10000, 0, 10000, 10, "待付款",
            "张三", "13800000000", "上海市", null,
            null, null, null, null, null, null,
            null, LocalDateTime.now(), List.of());

        PaymentChannelDO channel = new PaymentChannelDO();
        channel.setId(1L);
        channel.setChannelCode("wxpay");
        channel.setChannelName("微信支付");
        channel.setEnabled(1);

        when(orderApi.detail("FM202401010000001234")).thenReturn(order);
        when(paymentChannelMapper.selectOne(any())).thenReturn(channel);

        var req = new PaymentPrepareReq("FM202401010000001234", "wxpay");
        var resp = paymentPrepareService.prepare(req);

        assertNotNull(resp);
        assertEquals("wxpay", resp.channel());
        assertNotNull(resp.paymentNo());
        assertNotNull(resp.mockPayload());

        verify(paymentRecordMapper).insert(any(PaymentRecordDO.class));
    }
}
