package com.ferry.module.settlement.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("settlement_bill")
public class SettlementBillDO {
    private Long id;
    private Long tenantId;
    private Long merchantId;
    private String merchantName;
    private Integer orderAmountCent;
    private Integer commissionCent;
    private Integer payableCent;
    private Integer status;
}
