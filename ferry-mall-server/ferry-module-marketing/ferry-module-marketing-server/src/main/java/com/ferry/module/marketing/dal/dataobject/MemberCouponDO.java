package com.ferry.module.marketing.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("member_coupon")
public class MemberCouponDO {
    private Long id;
    private Long tenantId;
    private Long memberId;
    private Long couponId;
    private String couponName;
    private Integer discountCent;
    private Integer thresholdCent;
    private Integer status;
    private LocalDateTime usedTime;
    private LocalDateTime expireTime;
    private LocalDateTime createdAt;
}
