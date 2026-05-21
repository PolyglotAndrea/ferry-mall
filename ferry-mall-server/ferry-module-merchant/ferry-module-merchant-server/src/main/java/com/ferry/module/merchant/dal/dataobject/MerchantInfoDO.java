package com.ferry.module.merchant.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("merchant_info")
public class MerchantInfoDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String name;
    private String contactName;
    private String contactMobile;
    private String licenseNo;
    private Integer status;
}
