package com.ferry.module.store.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("live_room")
public class LiveRoomDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String name;
    private String coverUrl;
    private String anchorName;
    private String streamUrl;
    private Integer status;
    private LocalDateTime createdAt;
}
