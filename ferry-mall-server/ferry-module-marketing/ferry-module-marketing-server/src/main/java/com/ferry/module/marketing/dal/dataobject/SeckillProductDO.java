package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("seckill_product")
public class SeckillProductDO {
    private Long id;
    private Long tenantId;
    private Long activityId;
    private Long spuId;
    private Integer seckillPriceCent;
    private Integer stock;
    private Integer sold;
    private Integer status;
}
