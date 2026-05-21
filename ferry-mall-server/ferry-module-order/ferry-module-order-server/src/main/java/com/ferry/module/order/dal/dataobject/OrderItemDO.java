package com.ferry.module.order.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("order_item")
public class OrderItemDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long orderId;
    private Long spuId;
    private Long skuId;
    private Long storeId;
    private String productName;
    private String productImage;
    private Integer priceCent;
    private Integer quantity;
    private Integer totalCent;
}
