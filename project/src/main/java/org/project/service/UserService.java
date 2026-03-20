package org.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.User;
import org.project.model.UserWithRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService  {
    String getUserRoleNameById(Long userId);

    Boolean saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    User getUserByUsername(String username);

    UserWithRole  getUserWithRoleById(Long userId);

    IPage<User> PageList(int page, int size,String username,Long roleId);

    /**
     * 按手机号查询用户
     */
    User getUserByPhone(String phone);

    /**
     * 创建一个“个体用户”(unit_id 为空)，用于手机号验证码首次登录自动注册。
     */
    User createIndividualUserByPhone(String phone);

    /**
     * 更新用户信息
     */
     Boolean updateUserInfo(User user);

     /**
      * 删除用户
      */
     Boolean removeUserById(Long userId);
 
     /**
      * 启用/禁用用户
      */
     Boolean updateUserStatus(Long userId, Integer status);

     Boolean changePassword(String username,String oldPassword, String newPassword);

     User getUserByEmail(String email);
}
