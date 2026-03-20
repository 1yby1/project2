package org.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;

import org.project.model.LoginUser;
import org.project.model.User;
import org.project.service.SmsCodeService;
import org.project.service.UserService;
import org.project.util.JwtUtil;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SmsCodeService smsCodeService;

    @GetMapping("/all")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/list")
    public Result<IPage<User>> getPageList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int size, @RequestParam(required = false) String username, @RequestParam(required = false) Long roleId) {
        IPage<User> userPage = userService.PageList(page, size, username, roleId);
        return Result.success(userPage);
    }

    /**
     * 获取当前登录用户的信息
     */
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo(Authentication authentication) {
        // 从SecurityContextHolder中获取当前已认证的用户
        Long userId = getUserIdFromAuth(authentication);

        User user = userService.getUserById(userId);

        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 不返回密码等敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新当前登录用户的信息
     */
    @PutMapping("/info")
    public Result<Boolean> updateCurrentUserInfo(@RequestBody User user, Authentication authentication) {
        // 从SecurityContextHolder中获取当前已认证的用户
        Long userId = getUserIdFromAuth(authentication);
        // 假设用户名就是用户的唯一标识，可以根据实际情况修改
        User existingUser = userService.getUserById(userId);

        if (existingUser == null) {
            return Result.error(404, "用户不存在");
        }

        // 确保只能更新自己的信息
        user.setId(existingUser.getId());
        boolean success = userService.updateUserInfo(user);

        if (success) {
            return Result.success(true);
        } else {
            return Result.error(500, "更新失败");
        }
    }

    /**
     * 修改当前登录用户的密码
     */
    @PostMapping("/password")
    public Result<Boolean> changePassword(@RequestBody PasswordChangeDto passwordChangeDto, Authentication authentication) {
        // 从SecurityContextHolder中获取当前已认证的用户
        Long userId = getUserIdFromAuth(authentication);
        // 获取当前用户名
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            return Result.error(404, "用户不存在");
        }
        String username = currentUser.getUsername();

        // 调用服务层修改密码
        boolean success = userService.changePassword(username, passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());

        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "旧密码不正确");
        }
    }

    /**
     * 更新当前登录用户的手机号
     */
    @PutMapping("/phone")
    public Result<Boolean> updatePhone(@RequestBody PhoneChangeDto phoneChangeDto, Authentication authentication) {
        // 从SecurityContextHolder中获取当前已认证的用户
        Long userId = getUserIdFromAuth(authentication);

        // 获取当前用户信息
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            return Result.error(404, "用户不存在");
        }

        // 验证验证码
        boolean isCodeValid = smsCodeService.verifyCode(phoneChangeDto.getPhone(), phoneChangeDto.getCode());
        if (!isCodeValid) {
            return Result.error(400, "验证码错误或已过期");
        }

        // 检查新手机号是否已被其他用户使用
        User existingUser = userService.getUserByPhone(phoneChangeDto.getPhone());
        if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
            return Result.error(400, "该手机号已被其他用户使用");
        }

        // 更新用户手机号
        currentUser.setPhone(phoneChangeDto.getPhone());
        boolean success = userService.updateUserInfo(currentUser);

        if (success) {
            return Result.success(true);
        } else {
            return Result.error(500, "更新手机号失败");
        }
    }

    // 内部DTO类用于接收密码修改请求
    public static class PasswordChangeDto {
        private String oldPassword;
        private String newPassword;

        // getter and setter
        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    // 内部DTO类用于接收手机号修改请求
    public static class PhoneChangeDto {
        private String phone;
        private String code;

        // getter and setter
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // 内部DTO类用于接收邮箱修改请求
    public static class EmailChangeDto {
        private String email;

        // getter and setter
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 更新当前登录用户的邮箱
     */
    @PutMapping("/email")
    public Result<Boolean> updateEmail(@RequestBody EmailChangeDto emailChangeDto, Authentication authentication) {
        // 从SecurityContextHolder中获取当前已认证的用户
        Long userId = getUserIdFromAuth(authentication);
        // 获取当前用户信息
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            return Result.error(404, "用户不存在");
        }

        // 验证邮箱格式（简单验证）
        if (!emailChangeDto.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return Result.error(400, "邮箱格式不正确");
        }

        // 检查新邮箱是否已被其他用户使用
        User existingUser = userService.getUserByEmail(emailChangeDto.getEmail());
        if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
            return Result.error(400, "该邮箱已被其他用户使用");
        }

        // 更新用户邮箱
        currentUser.setEmail(emailChangeDto.getEmail());
        boolean success = userService.updateUserInfo(currentUser);

        if (success) {
            return Result.success(true);
        } else {
            return Result.error(500, "更新邮箱失败");
        }
    }

    private Long getUserIdFromAuth(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            if (loginUser.getUserId() == null) {
                throw new IllegalStateException("认证信息缺少用户ID");
            }
            return loginUser.getUserId();
        }

        // principal 不是 LoginUser 的情况下，不再依赖 UserDetails 类型判断
        throw new IllegalStateException("无法识别的认证信息类型：" + principal.getClass().getName());
    }
}
