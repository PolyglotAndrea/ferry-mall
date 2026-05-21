package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class SysLoginLogDO {
    private Long id;
    private Long tenantId;
    private Long userId;
    private Integer userType;
    private String username;
    private String ip;
    private String userAgent;
    private Integer result;
    private LocalDateTime createdAt;
}
