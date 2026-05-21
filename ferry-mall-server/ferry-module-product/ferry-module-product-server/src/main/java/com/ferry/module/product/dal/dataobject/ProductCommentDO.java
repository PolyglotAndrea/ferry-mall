package com.ferry.module.product.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("product_comment")
public class ProductCommentDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long spuId;
    private Long memberId;
    private String memberNickname;
    private String memberAvatar;
    private String content;
    private Integer rating;
    private String images;
    private Integer status;
    private LocalDateTime createdAt;
}
