package com.ferry.module.system.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_menu")
public class SysMenuDO {
    private Long id;
    private String name;
    private String permission;
    private Integer type;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String icon;
    private Integer status;
}
