package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("member_footprint")
public class MemberFootprintDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Long spuId;
    private String spuName;
    private String spuCover;
    private Integer priceCent;
    private LocalDateTime createdAt;
}
