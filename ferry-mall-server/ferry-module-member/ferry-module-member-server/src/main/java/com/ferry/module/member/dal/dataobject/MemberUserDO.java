package com.ferry.module.member.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("member_user")
public class MemberUserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String openid;
    private String unionid;
    private String mobile;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Integer status;
}
