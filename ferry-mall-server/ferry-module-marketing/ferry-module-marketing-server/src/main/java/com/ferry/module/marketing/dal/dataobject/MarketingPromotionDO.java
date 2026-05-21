package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("marketing_promotion")
public class MarketingPromotionDO {
    private Long id;
    private Long tenantId;
    private String name;
    private String promotionType;
    private String ruleJson;
    private Integer status;
}
