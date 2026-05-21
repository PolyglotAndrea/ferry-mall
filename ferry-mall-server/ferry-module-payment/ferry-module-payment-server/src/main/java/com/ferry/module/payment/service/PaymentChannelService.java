package com.ferry.module.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.payment.dal.dataobject.PaymentChannelDO;
import com.ferry.module.payment.dal.mapper.PaymentChannelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentChannelService {

    private final PaymentChannelMapper paymentChannelMapper;

    public PaymentChannelService(PaymentChannelMapper paymentChannelMapper) {
        this.paymentChannelMapper = paymentChannelMapper;
    }

    public PaymentChannelDO detail(Long id) {
        PaymentChannelDO channel = paymentChannelMapper.selectById(id);
        if (channel == null) {
            throw new FerryBusinessException(404, "支付渠道不存在");
        }
        return channel;
    }

    public PageResult<PaymentChannelDO> page(PageParam pageParam) {
        Page<PaymentChannelDO> page = paymentChannelMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<PaymentChannelDO>()
                .orderByDesc(PaymentChannelDO::getId));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentChannelDO create(PaymentChannelDO channel) {
        channel.setTenantId(TenantContext.getTenantId());
        channel.setCreatedAt(LocalDateTime.now());
        channel.setUpdatedAt(LocalDateTime.now());
        paymentChannelMapper.insert(channel);
        return channel;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentChannelDO update(Long id, PaymentChannelDO channel) {
        PaymentChannelDO existing = paymentChannelMapper.selectById(id);
        if (existing == null) {
            throw new FerryBusinessException(404, "支付渠道不存在");
        }
        existing.setChannelName(channel.getChannelName());
        existing.setEnabled(channel.getEnabled());
        existing.setConfigJson(channel.getConfigJson());
        existing.setUpdatedAt(LocalDateTime.now());
        paymentChannelMapper.updateById(existing);
        return existing;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PaymentChannelDO existing = paymentChannelMapper.selectById(id);
        if (existing == null) {
            throw new FerryBusinessException(404, "支付渠道不存在");
        }
        paymentChannelMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentChannelDO toggleEnabled(Long id) {
        PaymentChannelDO existing = paymentChannelMapper.selectById(id);
        if (existing == null) {
            throw new FerryBusinessException(404, "支付渠道不存在");
        }
        existing.setEnabled(existing.getEnabled() != null && existing.getEnabled() == 1 ? 0 : 1);
        existing.setUpdatedAt(LocalDateTime.now());
        paymentChannelMapper.updateById(existing);
        return existing;
    }
}
