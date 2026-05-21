package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.payment.api.dto.RefundCreateReq;
import com.ferry.module.payment.api.dto.RefundResp;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.dataobject.PaymentRefundDO;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import com.ferry.module.payment.dal.mapper.PaymentRefundMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentRefundService {

    private final PaymentRefundMapper paymentRefundMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    public PaymentRefundService(PaymentRefundMapper paymentRefundMapper,
                                 PaymentRecordMapper paymentRecordMapper) {
        this.paymentRefundMapper = paymentRefundMapper;
        this.paymentRecordMapper = paymentRecordMapper;
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
        if (req.refundAmountCent() > record.getAmountCent()) {
            throw new FerryBusinessException(400, "退款金额不能超过支付金额");
        }

        PaymentRefundDO refund = new PaymentRefundDO();
        refund.setRefundNo("REF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        refund.setPaymentNo(req.paymentNo());
        refund.setOrderNo(req.orderNo());
        refund.setRefundAmountCent(req.refundAmountCent());
        refund.setStatus(PaymentRefundDO.STATUS_PENDING);
        refund.setReason(req.reason());
        refund.setCreatedAt(LocalDateTime.now());
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
        refund.setStatus(PaymentRefundDO.STATUS_SUCCESS);
        refund.setUpdatedAt(LocalDateTime.now());
        paymentRefundMapper.updateById(refund);
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
