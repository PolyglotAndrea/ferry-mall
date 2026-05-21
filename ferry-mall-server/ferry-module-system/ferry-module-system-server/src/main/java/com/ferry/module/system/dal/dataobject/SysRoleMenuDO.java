package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class SysRoleMenuDO {
    private Long id;
    private Long roleId;
    private Long menuId;
}
