package com.ferry.module.payment.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.payment.dal.dataobject.PaymentRefundDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRefundMapper extends BaseMapper<PaymentRefundDO> {
}
