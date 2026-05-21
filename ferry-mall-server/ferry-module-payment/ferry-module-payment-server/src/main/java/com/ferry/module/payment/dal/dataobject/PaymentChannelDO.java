package com.ferry.module.payment.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("payment_channel")
public class PaymentChannelDO {
    private Long id;
    private Long tenantId;
    private String channelCode;
    private String channelName;
    private Integer enabled;
    private String configJson;
}
