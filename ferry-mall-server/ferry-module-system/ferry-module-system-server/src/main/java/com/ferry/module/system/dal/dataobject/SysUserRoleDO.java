package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysUserRoleDO {
    private Long id;
    private Long userId;
    private Long roleId;
}
