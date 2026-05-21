package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("bargain_record")
public class BargainRecordDO {
    private Long id;
    private Long tenantId;
    private Long activityId;
    private Long memberId;
    private Integer currentPriceCent;
    private Integer status;
    private LocalDateTime createdAt;
}
