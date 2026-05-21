package com.ferry.module.order.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.order.dal.dataobject.OrderInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfoDO> {
}
