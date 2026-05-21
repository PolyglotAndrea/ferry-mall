package com.ferry.module.logistics.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("logistics_trace")
public class LogisticsTraceDO {
    private Long id;
    private Long tenantId;
    private Long orderId;
    private String logisticsNo;
    private String company;
    private String traceContent;
}
