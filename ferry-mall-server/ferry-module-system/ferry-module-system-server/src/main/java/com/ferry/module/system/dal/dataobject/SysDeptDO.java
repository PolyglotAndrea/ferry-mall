package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dept")
public class SysDeptDO {
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String name;
    private Integer sort;
    private String leader;
    private Integer status;
}
