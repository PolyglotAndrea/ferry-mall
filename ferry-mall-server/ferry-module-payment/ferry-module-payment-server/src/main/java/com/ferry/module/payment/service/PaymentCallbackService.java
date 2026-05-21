package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.payment.api.dto.PaymentCallbackReq;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCallbackService {
    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderApi orderApi;

    public PaymentCallbackService(PaymentRecordMapper paymentRecordMapper,
                                  OrderApi orderApi) {
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderApi = orderApi;
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
            return;
        }

        if (req.success()) {
            record.setStatus(PaymentRecordDO.STATUS_SUCCESS);
            record.setThirdPartyNo(req.thirdPartyNo());
            record.setPaidAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);

            orderApi.pay(record.getOrderNo());
        } else {
            record.setStatus(PaymentRecordDO.STATUS_FAILED);
            record.setCallbackPayload(req.payload());
            paymentRecordMapper.updateById(record);
        }
    }
}
