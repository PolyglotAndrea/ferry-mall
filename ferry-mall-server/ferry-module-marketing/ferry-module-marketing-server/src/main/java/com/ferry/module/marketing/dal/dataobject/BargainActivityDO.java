package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("bargain_activity")
public class BargainActivityDO {
    private Long id;
    private Long tenantId;
    private String name;
    private Long spuId;
    private Integer originalPriceCent;
    private Integer floorPriceCent;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createdAt;
}
