package com.ferry.module.store.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("store_info")
public class StoreInfoDO {
    private Long id;
    private Long tenantId;
    private Long merchantId;
    private String name;
    private String logoUrl;
    private String description;
    private Integer status;
    private Double score;
}
