package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.payment.api.dto.PaymentPrepareReq;
import com.ferry.module.payment.api.dto.PaymentPrepareResp;
import com.ferry.module.payment.dal.dataobject.PaymentChannelDO;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentChannelMapper;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentPrepareService {
    private final PaymentChannelMapper paymentChannelMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderApi orderApi;

    public PaymentPrepareService(PaymentChannelMapper paymentChannelMapper,
                                 PaymentRecordMapper paymentRecordMapper,
                                 OrderApi orderApi) {
        this.paymentChannelMapper = paymentChannelMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderApi = orderApi;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentPrepareResp prepare(PaymentPrepareReq req) {
        OrderResp order = orderApi.detail(req.orderNo());
        if (order == null) {
            throw new FerryBusinessException(404, "订单不存在");
        }

        PaymentChannelDO channel = resolveChannel(req.channelCode());

        PaymentRecordDO record = new PaymentRecordDO();
        record.setPaymentNo("PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        record.setOrderNo(req.orderNo());
        record.setChannel(channel.getChannelCode());
        record.setAmountCent(order.payAmountCent());
        record.setStatus(PaymentRecordDO.STATUS_PENDING);
        paymentRecordMapper.insert(record);

        return new PaymentPrepareResp(record.getPaymentNo(), channel.getChannelCode(),
            buildMockPayload(record.getPaymentNo(), order.payAmountCent()));
    }

    private PaymentChannelDO resolveChannel(String channelCode) {
        LambdaQueryWrapper<PaymentChannelDO> wrapper = new LambdaQueryWrapper<PaymentChannelDO>()
            .eq(PaymentChannelDO::getEnabled, 1);
        if (channelCode != null && !channelCode.isBlank()) {
            wrapper.eq(PaymentChannelDO::getChannelCode, channelCode);
        } else {
            wrapper.orderByAsc(PaymentChannelDO::getId);
        }
        PaymentChannelDO channel = paymentChannelMapper.selectOne(wrapper.last("limit 1"));
        if (channel == null) {
            throw new FerryBusinessException(503, "暂无可用支付通道");
        }
        return channel;
    }

    private String buildMockPayload(String paymentNo, int amountCent) {
        return String.format("{\"paymentNo\":\"%s\",\"amount\":%.2f,\"mock\":true}",
            paymentNo, amountCent / 100.0);
    }
}
