package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_tenant")
public class SysTenantDO {
    private Long id;
    private String name;
    private String contactName;
    private String contactMobile;
    private Long packageId;
    private LocalDateTime expireTime;
    private Integer accountCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
