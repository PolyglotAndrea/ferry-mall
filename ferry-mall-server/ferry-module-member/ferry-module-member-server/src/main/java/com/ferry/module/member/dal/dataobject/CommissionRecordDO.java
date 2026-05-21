package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("commission_record")
public class CommissionRecordDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long memberId;
    private String orderNo;
    private Long commissionCent;
    private Integer type;
    private Integer status;
    private LocalDateTime createdAt;
}
