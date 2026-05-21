package com.ferry.module.product.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_category")
public class ProductCategoryDO {
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String name;
    private Integer sort;
    private Integer visible;
}
