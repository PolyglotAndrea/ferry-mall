package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_exchange")
public class PointsExchangeDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Long productId;
    private String productName;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;
}
