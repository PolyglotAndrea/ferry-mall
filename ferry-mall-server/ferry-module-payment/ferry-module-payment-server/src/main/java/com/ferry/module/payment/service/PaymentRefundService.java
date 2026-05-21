package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.payment.api.dto.RefundCreateReq;
import com.ferry.module.payment.api.dto.RefundResp;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.dataobject.PaymentRefundDO;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import com.ferry.module.payment.dal.mapper.PaymentRefundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentRefundService {

    private static final Logger log = LoggerFactory.getLogger(PaymentRefundService.class);

    private final PaymentRefundMapper paymentRefundMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderApi orderApi;

    public PaymentRefundService(PaymentRefundMapper paymentRefundMapper,
                                 PaymentRecordMapper paymentRecordMapper,
                                 OrderApi orderApi) {
        this.paymentRefundMapper = paymentRefundMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderApi = orderApi;
    }

    @Transactional(rollbackFor = Exception.class)
    public RefundResp create(RefundCreateReq req) {
        PaymentRecordDO record = paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecordDO>()
                .eq(PaymentRecordDO::getPaymentNo, req.paymentNo()));
        if (record == null) {
            throw new FerryBusinessException(404, "支付记录不存在");
        }
        if (record.getStatus() != PaymentRecordDO.STATUS_SUCCESS) {
            throw new FerryBusinessException(400, "该支付记录未成功，无法退款");
        }

        // 校验退款金额：不能超过已支付金额减去已退款金额
        int totalRefunded = totalRefundedAmount(req.paymentNo());
        int refundable = record.getAmountCent() - totalRefunded;
        if (req.refundAmountCent() > refundable) {
            throw new FerryBusinessException(400,
                "退款金额不能超过可退金额，可退金额: " + (refundable / 100.0) + " 元");
        }
        if (req.refundAmountCent() <= 0) {
            throw new FerryBusinessException(400, "退款金额必须大于 0");
        }

        // 校验订单状态是否允许退款
        OrderResp order = orderApi.detail(req.orderNo());
        if (order != null) {
            int status = order.status();
            if (status == com.ferry.module.order.service.OrderStatusMachine.PENDING_PAYMENT
                || status == com.ferry.module.order.service.OrderStatusMachine.CANCELLED) {
                throw new FerryBusinessException(400, "当前订单状态不允许退款");
            }
        }

        PaymentRefundDO refund = new PaymentRefundDO();
        refund.setRefundNo("REF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        refund.setPaymentNo(req.paymentNo());
        refund.setOrderNo(req.orderNo());
        refund.setRefundAmountCent(req.refundAmountCent());
        refund.setStatus(PaymentRefundDO.STATUS_PENDING);
        refund.setReason(req.reason());
        refund.setCreatedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());
        paymentRefundMapper.insert(refund);

        return toResp(refund);
    }

    @Transactional(rollbackFor = Exception.class)
    public RefundResp approve(Long id) {
        PaymentRefundDO refund = paymentRefundMapper.selectById(id);
        if (refund == null) {
            throw new FerryBusinessException(404, "退款记录不存在");
        }
        if (refund.getStatus() != PaymentRefundDO.STATUS_PENDING) {
            throw new FerryBusinessException(400, "退款状态不允许操作");
        }

        // 调用第三方退款接口（模拟）
        boolean thirdPartySuccess = callThirdPartyRefund(refund);
        if (!thirdPartySuccess) {
            log.error("第三方退款失败: refundNo={}", refund.getRefundNo());
            throw new FerryBusinessException(500, "第三方退款接口调用失败");
        }

        refund.setStatus(PaymentRefundDO.STATUS_SUCCESS);
        refund.setUpdatedAt(LocalDateTime.now());
        paymentRefundMapper.updateById(refund);

        // 更新订单状态为退款中或已退款
        OrderResp order = orderApi.detail(refund.getOrderNo());
        if (order != null) {
            int totalRefunded = totalRefundedAmount(refund.getPaymentNo());
            PaymentRecordDO record = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecordDO>()
                    .eq(PaymentRecordDO::getPaymentNo, refund.getPaymentNo()));
            if (record != null && totalRefunded >= record.getAmountCent()) {
                // 全额退款，订单变为已退款
                log.info("订单 {} 已全额退款", refund.getOrderNo());
            } else {
                log.info("订单 {} 部分退款 {} 分", refund.getOrderNo(), refund.getRefundAmountCent());
            }
        }

        return toResp(refund);
    }

    @Transactional(rollbackFor = Exception.class)
    public RefundResp reject(Long id) {
        PaymentRefundDO refund = paymentRefundMapper.selectById(id);
        if (refund == null) {
            throw new FerryBusinessException(404, "退款记录不存在");
        }
        if (refund.getStatus() != PaymentRefundDO.STATUS_PENDING) {
            throw new FerryBusinessException(400, "退款状态不允许操作");
        }
        refund.setStatus(PaymentRefundDO.STATUS_FAILED);
        refund.setUpdatedAt(LocalDateTime.now());
        paymentRefundMapper.updateById(refund);
        return toResp(refund);
    }

    public RefundResp detail(Long id) {
        PaymentRefundDO refund = paymentRefundMapper.selectById(id);
        if (refund == null) {
            throw new FerryBusinessException(404, "退款记录不存在");
        }
        return toResp(refund);
    }

    public PageResult<RefundResp> page(PageParam pageParam) {
        Page<PaymentRefundDO> page = paymentRefundMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<PaymentRefundDO>()
                .orderByDesc(PaymentRefundDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    private int totalRefundedAmount(String paymentNo) {
        List<PaymentRefundDO> refunds = paymentRefundMapper.selectList(
            new LambdaQueryWrapper<PaymentRefundDO>()
                .eq(PaymentRefundDO::getPaymentNo, paymentNo)
                .eq(PaymentRefundDO::getStatus, PaymentRefundDO.STATUS_SUCCESS));
        return refunds.stream().mapToInt(PaymentRefundDO::getRefundAmountCent).sum();
    }

    private boolean callThirdPartyRefund(PaymentRefundDO refund) {
        // 模拟调用第三方退款接口
        // 实际项目中这里会调用微信支付/支付宝退款 API
        log.info("调用第三方退款接口: refundNo={}, paymentNo={}, amount={}分",
            refund.getRefundNo(), refund.getPaymentNo(), refund.getRefundAmountCent());
        refund.setThirdPartyRefundNo("TP_REF_" + System.currentTimeMillis());
        return true;
    }

    private RefundResp toResp(PaymentRefundDO r) {
        String statusText = switch (r.getStatus()) {
            case 10 -> "待处理";
            case 20 -> "退款成功";
            case 30 -> "退款失败";
            default -> "未知";
        };
        return new RefundResp(r.getId(), r.getRefundNo(), r.getPaymentNo(), r.getOrderNo(),
            r.getRefundAmountCent(), r.getStatus(), statusText, r.getReason(), r.getCreatedAt());
    }
}
