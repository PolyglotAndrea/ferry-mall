package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("member_level")
public class MemberLevelDO {
    private Long id;
    private Long tenantId;
    private String name;
    private Integer minPoints;
    private Integer discountPercent;
    private Integer status;
}
