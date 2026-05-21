package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.payment.api.dto.PaymentChannelConfig;
import com.ferry.module.payment.api.dto.PaymentPrepareReq;
import com.ferry.module.payment.api.dto.PaymentPrepareResp;
import com.ferry.module.payment.dal.dataobject.PaymentChannelDO;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentChannelMapper;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentPrepareService {
    private static final Logger log = LoggerFactory.getLogger(PaymentPrepareService.class);

    public static final String CHANNEL_WXPAY = "wxpay";
    public static final String CHANNEL_ALIPAY = "alipay";
    public static final String CHANNEL_MOCK = "mock";

    private final PaymentChannelMapper paymentChannelMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderApi orderApi;
    private final ObjectMapper objectMapper;

    public PaymentPrepareService(PaymentChannelMapper paymentChannelMapper,
                                 PaymentRecordMapper paymentRecordMapper,
                                 OrderApi orderApi) {
        this.paymentChannelMapper = paymentChannelMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderApi = orderApi;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentPrepareResp prepare(PaymentPrepareReq req) {
        OrderResp order = orderApi.detail(req.orderNo());
        if (order == null) {
            throw new FerryBusinessException(404, "订单不存在");
        }
        if (order.status() != com.ferry.module.order.service.OrderStatusMachine.PENDING_PAYMENT) {
            throw new FerryBusinessException(400, "订单状态不允许支付");
        }

        PaymentChannelDO channel = resolveChannel(req.channelCode());
        String paymentNo = generatePaymentNo();

        PaymentRecordDO record = new PaymentRecordDO();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(req.orderNo());
        record.setChannel(channel.getChannelCode());
        record.setAmountCent(order.payAmountCent());
        record.setStatus(PaymentRecordDO.STATUS_PENDING);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        paymentRecordMapper.insert(record);

        String payload = buildChannelPayload(channel, record, order);
        return new PaymentPrepareResp(record.getPaymentNo(), channel.getChannelCode(), payload);
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

    private String buildChannelPayload(PaymentChannelDO channel, PaymentRecordDO record, OrderResp order) {
        return switch (channel.getChannelCode()) {
            case CHANNEL_WXPAY -> buildWxpayPayload(channel, record, order);
            case CHANNEL_ALIPAY -> buildAlipayPayload(channel, record, order);
            case CHANNEL_MOCK -> buildMockPayload(record.getPaymentNo(), order.payAmountCent());
            default -> buildMockPayload(record.getPaymentNo(), order.payAmountCent());
        };
    }

    private String buildWxpayPayload(PaymentChannelDO channel, PaymentRecordDO record, OrderResp order) {
        PaymentChannelConfig config = parseConfig(channel.getConfigJson());
        String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String prepayId = "wx_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Map<String, Object> payload = Map.of(
            "appId", config.appId() != null ? config.appId() : "wx_mock_appid",
            "timeStamp", String.valueOf(System.currentTimeMillis() / 1000),
            "nonceStr", nonceStr,
            "package", "prepay_id=" + prepayId,
            "signType", "RSA",
            "paySign", "mock_sign_" + nonceStr,
            "paymentNo", record.getPaymentNo()
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("微信支付参数序列化失败", e);
            throw new FerryBusinessException(500, "支付参数构造失败");
        }
    }

    private String buildAlipayPayload(PaymentChannelDO channel, PaymentRecordDO record, OrderResp order) {
        PaymentChannelConfig config = parseConfig(channel.getConfigJson());
        Map<String, Object> payload = Map.of(
            "appId", config.appId() != null ? config.appId() : "alipay_mock_appid",
            "outTradeNo", record.getPaymentNo(),
            "totalAmount", String.format("%.2f", order.payAmountCent() / 100.0),
            "subject", "Ferry Mall 订单",
            "productCode", "QUICK_MSECURITY_PAY",
            "paymentNo", record.getPaymentNo(),
            "tradeNo", "alipay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("支付宝参数序列化失败", e);
            throw new FerryBusinessException(500, "支付参数构造失败");
        }
    }

    private String buildMockPayload(String paymentNo, int amountCent) {
        return String.format("{\"paymentNo\":\"%s\",\"amount\":%.2f,\"mock\":true}",
            paymentNo, amountCent / 100.0);
    }

    private PaymentChannelConfig parseConfig(String configJson) {
        if (configJson == null || configJson.isBlank()) {
            return new PaymentChannelConfig(null, null, null, null, null, null);
        }
        try {
            return objectMapper.readValue(configJson, PaymentChannelConfig.class);
        } catch (JsonProcessingException e) {
            log.warn("支付渠道配置解析失败: {}", configJson);
            return new PaymentChannelConfig(null, null, null, null, null, null);
        }
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
            + String.format("%04d", (int) (Math.random() * 10000));
    }
}
