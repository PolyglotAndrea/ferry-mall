package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("groupon_record")
public class GrouponRecordDO {
    private Long id;
    private Long tenantId;
    private Long activityId;
    private Long memberId;
    private Integer status;
    private LocalDateTime createdAt;
}
