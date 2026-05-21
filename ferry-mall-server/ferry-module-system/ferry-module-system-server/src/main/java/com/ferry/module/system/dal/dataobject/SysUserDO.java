package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUserDO {
    private Long id;
    private Long tenantId;
    private String username;
    private String password;
    private String nickname;
    private Integer status;
}
