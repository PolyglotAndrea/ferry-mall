package com.ferry.module.payment.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("payment_record")
public class PaymentRecordDO {
    public static final int STATUS_PENDING = 10;
    public static final int STATUS_SUCCESS = 20;
    public static final int STATUS_FAILED = 30;
    public static final int STATUS_CLOSED = 40;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String paymentNo;
    private String orderNo;
    private String channel;
    private Integer amountCent;
    private Integer status;
    private String thirdPartyNo;
    private String callbackPayload;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
