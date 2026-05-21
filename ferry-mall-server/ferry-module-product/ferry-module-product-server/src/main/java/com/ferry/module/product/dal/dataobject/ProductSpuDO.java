package com.ferry.module.product.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_spu")
public class ProductSpuDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long categoryId;
    private Long storeId;
    private String name;
    private String subtitle;
    private String coverUrl;
    private Integer priceCent;
    private Integer marketPriceCent;
    private Integer stock;
    private Integer sales;
    private Integer status;
}
