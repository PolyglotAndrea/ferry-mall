package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("commission_withdraw")
public class CommissionWithdrawDO {
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Integer amountCent;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
