package org.project.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.project.mapper.UserMapper;
import org.project.model.User;
import org.project.model.UserWithRole;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Override
    public Boolean saveUser(User user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public String getUserRoleNameById(Long userId) {
        return userMapper.getUserRoleNameById(userId);
    }
    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
    @Override
    public UserWithRole getUserWithRoleById(Long userId) {
        return userMapper.getUserWithRoleById(userId);
    }

    @Override
    public IPage<User> PageList(int page, int size, String username, Long roleId) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectUserPageWithUnit(pageParam, username, roleId);
    }


    @Override
    public User getUserByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone, phone);
        return userMapper.selectOne(qw);
    }

    @Override
    public User createIndividualUserByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        User exists = getUserByPhone(phone);
        if (exists != null) {
            return exists;
        }

        User user = new User();
        user.setPhone(phone);
        // 这里 username 用 phone 做默认值，保证唯一性；后续可提供“完善资料”接口修改
        user.setUsername(phone);
        // 密码留空或随机值皆可；目前你的 login/password 登录会直接比对字符串，留空可以避免误登录
        user.setPassword("");
        user.setRoleId(3L);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }
    @Override
    public Boolean updateUserInfo(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        // 只更新允许修改的字段，避免敏感信息被篡改
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            return false;
        }

        // 更新用户信息
        existingUser.setRealName(user.getRealName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setIdCard(user.getIdCard());
        existingUser.setUnitId(user.getUnitId());

        return userMapper.updateById(existingUser) > 0;
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getEmail, email);
        return userMapper.selectOne(qw);
    }

    @Override
    public Boolean removeUserById(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }

    @Override
    public Boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }
    @Override
    public Boolean changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }

        // 获取用户信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 设置新密码（加密后）
        user.setPassword(passwordEncoder.encode(newPassword));

        // 更新密码
        return userMapper.updateById(user) > 0;
    }

}
