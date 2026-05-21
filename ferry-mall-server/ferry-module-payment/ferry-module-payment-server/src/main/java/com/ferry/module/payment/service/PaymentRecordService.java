package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.payment.dal.dataobject.PaymentRecordDO;
import com.ferry.module.payment.dal.mapper.PaymentRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentRecordService {

    private final PaymentRecordMapper paymentRecordMapper;

    public PaymentRecordService(PaymentRecordMapper paymentRecordMapper) {
        this.paymentRecordMapper = paymentRecordMapper;
    }

    public PaymentRecordDO detail(Long id) {
        return paymentRecordMapper.selectById(id);
    }

    public PaymentRecordDO detailByPaymentNo(String paymentNo) {
        return paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecordDO>()
                .eq(PaymentRecordDO::getPaymentNo, paymentNo));
    }

    public PageResult<PaymentRecordDO> page(PageParam pageParam, String orderNo, String channel, Integer status) {
        LambdaQueryWrapper<PaymentRecordDO> wrapper = new LambdaQueryWrapper<PaymentRecordDO>()
            .orderByDesc(PaymentRecordDO::getId);
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.eq(PaymentRecordDO::getOrderNo, orderNo);
        }
        if (channel != null && !channel.isBlank()) {
            wrapper.eq(PaymentRecordDO::getChannel, channel);
        }
        if (status != null) {
            wrapper.eq(PaymentRecordDO::getStatus, status);
        }
        Page<PaymentRecordDO> page = paymentRecordMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }
}
