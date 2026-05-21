package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.payment.api.dto.PaymentCallbackReq;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCallbackService {
    private static final Logger log = LoggerFactory.getLogger(PaymentCallbackService.class);

    public static final String CHANNEL_WXPAY = "wxpay";
    public static final String CHANNEL_ALIPAY = "alipay";
    public static final String CHANNEL_MOCK = "mock";

    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderApi orderApi;
    private final ObjectMapper objectMapper;

    public PaymentCallbackService(PaymentRecordMapper paymentRecordMapper,
                                  OrderApi orderApi) {
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderApi = orderApi;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional(rollbackFor = Exception.class)
    public void callback(PaymentCallbackReq req) {
        PaymentRecordDO record = paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecordDO>()
                .eq(PaymentRecordDO::getPaymentNo, req.paymentNo()));
        if (record == null) {
            throw new FerryBusinessException(404, "支付记录不存在");
        }
        if (record.getStatus() != PaymentRecordDO.STATUS_PENDING) {
            log.warn("支付记录状态不是待支付，跳过回调处理: paymentNo={}, status={}",
                req.paymentNo(), record.getStatus());
            return;
        }

        boolean verified = verifyCallback(record.getChannel(), req);
        if (!verified) {
            log.error("回调验签失败: paymentNo={}", req.paymentNo());
            throw new FerryBusinessException(400, "回调验签失败");
        }

        if (req.success()) {
            record.setStatus(PaymentRecordDO.STATUS_SUCCESS);
            record.setThirdPartyNo(req.thirdPartyNo());
            record.setPaidAt(LocalDateTime.now());
            record.setCallbackPayload(req.payload());
            record.setUpdatedAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);

            orderApi.pay(record.getOrderNo());
            log.info("支付成功回调处理完成: paymentNo={}, orderNo={}",
                req.paymentNo(), record.getOrderNo());
        } else {
            record.setStatus(PaymentRecordDO.STATUS_FAILED);
            record.setCallbackPayload(req.payload());
            record.setUpdatedAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            log.info("支付失败回调处理完成: paymentNo={}", req.paymentNo());
        }
    }

    /**
     * 处理微信支付回调通知（XML/JSON 格式）
     */
    @Transactional(rollbackFor = Exception.class)
    public void wxpayCallback(String body) {
        log.info("收到微信支付回调: {}", body);
        try {
            JsonNode node = objectMapper.readTree(body);
            String paymentNo = node.has("out_trade_no") ? node.get("out_trade_no").asText() : null;
            String transactionId = node.has("transaction_id") ? node.get("transaction_id").asText() : null;
            String resultCode = node.has("result_code") ? node.get("result_code").asText() : "";

            if (paymentNo == null) {
                throw new FerryBusinessException(400, "微信支付回调缺少订单号");
            }

            PaymentRecordDO record = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecordDO>()
                    .eq(PaymentRecordDO::getPaymentNo, paymentNo));
            if (record == null) {
                throw new FerryBusinessException(404, "支付记录不存在");
            }
            if (record.getStatus() != PaymentRecordDO.STATUS_PENDING) {
                return;
            }

            boolean success = "SUCCESS".equalsIgnoreCase(resultCode);
            if (success) {
                record.setStatus(PaymentRecordDO.STATUS_SUCCESS);
                record.setThirdPartyNo(transactionId);
                record.setPaidAt(LocalDateTime.now());
                record.setCallbackPayload(body);
                record.setUpdatedAt(LocalDateTime.now());
                paymentRecordMapper.updateById(record);
                orderApi.pay(record.getOrderNo());
                log.info("微信支付回调处理成功: paymentNo={}", paymentNo);
            } else {
                record.setStatus(PaymentRecordDO.STATUS_FAILED);
                record.setCallbackPayload(body);
                record.setUpdatedAt(LocalDateTime.now());
                paymentRecordMapper.updateById(record);
                log.warn("微信支付回调返回失败: paymentNo={}", paymentNo);
            }
        } catch (JsonProcessingException e) {
            log.error("微信支付回调解析失败", e);
            throw new FerryBusinessException(400, "回调数据解析失败");
        }
    }

    /**
     * 处理支付宝回调通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void alipayCallback(Map<String, String> params) {
        log.info("收到支付宝回调: {}", params);
        String paymentNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        if (paymentNo == null) {
            throw new FerryBusinessException(400, "支付宝回调缺少订单号");
        }

        PaymentRecordDO record = paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecordDO>()
                .eq(PaymentRecordDO::getPaymentNo, paymentNo));
        if (record == null) {
            throw new FerryBusinessException(404, "支付记录不存在");
        }
        if (record.getStatus() != PaymentRecordDO.STATUS_PENDING) {
            return;
        }

        boolean success = "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);
        if (success) {
            record.setStatus(PaymentRecordDO.STATUS_SUCCESS);
            record.setThirdPartyNo(tradeNo);
            record.setPaidAt(LocalDateTime.now());
            try {
                record.setCallbackPayload(objectMapper.writeValueAsString(params));
            } catch (JsonProcessingException e) {
                record.setCallbackPayload(params.toString());
            }
            record.setUpdatedAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            orderApi.pay(record.getOrderNo());
            log.info("支付宝回调处理成功: paymentNo={}", paymentNo);
        } else {
            record.setStatus(PaymentRecordDO.STATUS_FAILED);
            try {
                record.setCallbackPayload(objectMapper.writeValueAsString(params));
            } catch (JsonProcessingException e) {
                record.setCallbackPayload(params.toString());
            }
            record.setUpdatedAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            log.warn("支付宝回调返回失败状态: paymentNo={}, status={}", paymentNo, tradeStatus);
        }
    }

    private boolean verifyCallback(String channel, PaymentCallbackReq req) {
        if (CHANNEL_MOCK.equals(channel)) {
            return true;
        }
        // 微信支付/支付宝实际项目中需要验签
        // 这里简化处理，实际应使用对应 SDK 验证签名
        log.debug("回调验签: channel={}, paymentNo={}", channel, req.paymentNo());
        return true;
    }
}
