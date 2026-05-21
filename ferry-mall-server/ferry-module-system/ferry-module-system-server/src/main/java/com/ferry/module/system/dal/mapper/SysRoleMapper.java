package com.ferry.module.system.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ferry.module.system.dal.dataobject.SysRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleDO> {

    @Select("SELECT r.* FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<SysRoleDO> selectListByUserId(@Param("userId") Long userId);
}