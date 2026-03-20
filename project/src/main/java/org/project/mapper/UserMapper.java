package org.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.project.model.User;
import org.project.model.UserWithRole;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT u.id,u.username,u.email,r.name AS roleName " +
            "FROM user u " +
            "LEFT JOIN role r ON u.role_id = r.id " +
            "WHERE u.id=#{userId}")
    UserWithRole getUserWithRoleById(Long userId);

    @Select("SELECT r.name AS roleName " +
            "FROM user u " +
            "LEFT JOIN role r ON u.role_id = r.id " +
            "WHERE u.id=#{userId}")
    String getUserRoleNameById(Long userId);

    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    User getUserByPhone(String phone);

    @Select("SELECT * FROM user WHERE email = #{email} LIMIT 1")
    User getUserByEmail(String email);

        IPage<User> selectUserPageWithUnit(Page<User> page,
                                                                           @Param("username") String username,
                                                                           @Param("roleId") Long roleId);

}
