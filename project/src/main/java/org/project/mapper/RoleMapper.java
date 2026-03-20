package org.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.project.model.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    // Define additional methods specific to RoleMapper if needed
}
