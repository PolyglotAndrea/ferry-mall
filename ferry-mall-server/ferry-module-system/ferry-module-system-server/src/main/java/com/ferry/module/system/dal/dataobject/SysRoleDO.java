package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role")
public class SysRoleDO {
    private Long id;
    private Long tenantId;
    private String name;
    private String code;
    private Integer dataScope;
    private Integer status;
}
