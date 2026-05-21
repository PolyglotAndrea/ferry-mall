package com.ferry.module.payment.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("payment_refund")
public class PaymentRefundDO {
    private Long id;
    private Long tenantId;
    private String refundNo;
    private String paymentNo;
    private String orderNo;
    private Integer refundAmountCent;
    public static final int STATUS_PENDING = 10;
    public static final int STATUS_SUCCESS = 20;
    public static final int STATUS_FAILED = 30;
    private Integer status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
