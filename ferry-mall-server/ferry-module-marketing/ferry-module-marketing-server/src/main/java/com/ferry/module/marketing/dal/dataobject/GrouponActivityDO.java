package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("groupon_activity")
public class GrouponActivityDO {
    private Long id;
    private Long tenantId;
    private String name;
    private Long spuId;
    private Integer grouponPriceCent;
    private Integer requireCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createdAt;
}
