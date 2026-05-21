package com.ferry.module.member.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberUserMapper extends BaseMapper<MemberUserDO> {
}
