package org.project.service.Impl;

import org.project.model.LoginUser;
import org.project.model.User;
import org.project.model.UserWithRole;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        UserWithRole userWithRole = userService.getUserWithRoleById(user.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setRoleName(userWithRole != null ? userWithRole.getRoleName() : null);
        return loginUser;
    }

    public LoginUser loadUserById(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        UserWithRole userWithRole = userService.getUserWithRoleById(user.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        // JWT 场景不需要密码，但为了满足接口，留空即可
        loginUser.setPassword("");
        loginUser.setRoleName(userWithRole != null ? userWithRole.getRoleName() : null);
        return loginUser;
    }
}

