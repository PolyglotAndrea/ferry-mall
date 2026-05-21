package com.ferry.module.product.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.product.dal.dataobject.ProductCommentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCommentMapper extends BaseMapper<ProductCommentDO> {
}
