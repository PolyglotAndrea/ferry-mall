package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("commission_user")
public class CommissionUserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Long parentId;
    private Long totalCommissionCent;
    private Long availableCommissionCent;
    private Integer status;
    private LocalDateTime createdAt;
}
