package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("member_integral_record")
public class MemberIntegralRecordDO {
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Integer changeCount;
    private Integer currentPoints;
    private String reason;
    private Integer type;
    private LocalDateTime createdAt;
}
