package com.ferry.module.order.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("order_info")
public class OrderInfoDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String orderNo;
    private Long memberId;
    private Integer totalAmountCent;
    private Integer discountAmountCent;
    private Integer payAmountCent;
    private Integer status;
    private String receiverName;
    private String receiverMobile;
    private String receiverAddress;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
