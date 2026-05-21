package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operate_log")
public class SysOperateLogDO {
    private Long id;
    private Long tenantId;
    private Long userId;
    private String module;
    private String name;
    private Integer type;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String responseBody;
    private String userIp;
    private Integer duration;
    private Integer result;
    private LocalDateTime createdAt;
}
