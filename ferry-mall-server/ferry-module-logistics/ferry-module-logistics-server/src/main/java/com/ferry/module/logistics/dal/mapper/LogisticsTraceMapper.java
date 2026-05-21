package com.ferry.module.logistics.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.logistics.dal.dataobject.LogisticsTraceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogisticsTraceMapper extends BaseMapper<LogisticsTraceDO> {
}
