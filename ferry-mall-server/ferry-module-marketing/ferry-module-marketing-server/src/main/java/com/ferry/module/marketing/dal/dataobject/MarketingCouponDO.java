package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("marketing_coupon")
public class MarketingCouponDO {
    private Long id;
    private Long tenantId;
    private String name;
    private Integer discountCent;
    private Integer thresholdCent;
    private Integer totalCount;
    private Integer receivedCount;
    private Integer status;
}
