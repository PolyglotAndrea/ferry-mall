package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_file")
public class SysFileDO {
    private Long id;
    private Long tenantId;
    private String name;
    private String path;
    private String url;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
}
