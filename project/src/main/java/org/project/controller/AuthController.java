package org.project.controller;

import jakarta.validation.Valid;
import org.project.model.Dto.AuthDto.*;
import org.project.model.User;
import org.project.service.SmsCodeService;
import org.project.service.UnitService;
import org.project.service.UserService;
import org.project.util.JwtUtil;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<LoginResponse> loginWithUsernameAndPassword(@Valid @RequestBody UsernamePasswordDto usernamePasswordDto) {
        // Implement login logic here
        // This is a placeholder implementation

        User user = userService.getUserByUsername(usernamePasswordDto.getUsername());
        if (user != null) {
            if (passwordEncoder.matches(usernamePasswordDto.getPassword(), user.getPassword())) {
                // Generate JWT token
                String token = JwtUtil.generateToken(user.getId());
                String unitName=unitService.getUnitNameById(user.getUnitId());
                // Build a response DTO that contains token and safe user info (no password)
                String role=userService.getUserRoleNameById(user.getId());
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token);
                loginResponse.setUserId(user.getId());
                if(user.getUnitId()!=null) {
                    loginResponse.setUnitId(user.getUnitId());
                }
                else{
                    loginResponse.setUnitId(0L);
                }
                loginResponse.setUnitName(unitName);
                loginResponse.setUsername(user.getUsername());
                loginResponse.setRoleName(role);

                return Result.success(loginResponse);
            } else {
                return Result.error(400, "Invalid password");
            }
        } else {
            return Result.error(400, "User not found");
        }
    }
    @PostMapping("/login/phone")
    public Result<LoginResponse> loginWithPhoneAndPassword(@Valid @RequestBody PhonePasswordLoginDto phonePasswordLoginDto) {
        User user = userService.getUserByPhone(phonePasswordLoginDto.getPhone());
        if(user!=null){
            if(passwordEncoder.matches(phonePasswordLoginDto.getPassword(), user.getPassword())){
                String token = JwtUtil.generateToken(user.getId());
                String role=userService.getUserRoleNameById(user.getId());
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token);
                loginResponse.setUserId(user.getId());
                loginResponse.setUsername(user.getUsername());
                loginResponse.setRoleName(role);
                return Result.success(loginResponse);
            }else{
                return Result.error(400, "Invalid password");
            }
        }else{
            return Result.error(400, "User not found");
        }
    }
    @GetMapping("/me")
    public Result<User> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract token from header
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);
        if (user != null) {
            user.setPassword(null); // Do not expose password
            return Result.success(user);
        } else {
            return Result.error(404, "User not found");
        }
    }
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody RegisterRequestDto registerDto) {
        // Implement registration logic here
        // This is a placeholder implementation
        User existingUser = userService.getUserByUsername(registerDto.getUsername());
        if (existingUser != null) {
            return Result.error(400, "Username already exists");
        }
        boolean ok = smsCodeService.verifyCode(registerDto.getPhone(), registerDto.getCode());
        if (!ok) {
            return Result.error(400, "验证码错误或已过期");
        }

        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        // 加密密码后再保存
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setRoleId(3L); // Assuming roleId is optional, you can remove this if not needed
        newUser.setPhone(registerDto.getPhone());
        newUser.setStatus(1);
        // Set other fields as necessary, e.g., email, roleId, etc.

        boolean isSaved = userService.saveUser(newUser);
        if (isSaved) {
            return Result.success("User registered successfully");
        } else {
            return Result.error(500, "Failed to register user");
        }
    }

    /**
     * 发送手机验证码（用于登录/注册）
     *
     * 注意：当前为便于联调，接口会返回验证码；接入真实短信平台后应改为不返回。
     */
    @PostMapping("/phone/code")
    public Result<String> sendPhoneCode(@Valid @RequestBody PhoneCodeSendDto dto) {
        try {
            String code = smsCodeService.sendLoginOrRegisterCode(dto.getPhone());
            return Result.success(code);
        } catch (IllegalStateException e) {
            return Result.error(429, e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 手机号+验证码 登录（若手机号未注册则自动创建为“个体用户”，unit_id 为空）
     */
    @PostMapping("/phone/login")
    public Result<LoginResponse> loginByPhoneCode(@Valid @RequestBody PhoneCodeLoginDto dto) {
        boolean ok = smsCodeService.verifyCode(dto.getPhone(), dto.getCode());
        if (!ok) {
            return Result.error(400, "验证码错误或已过期");
        }

        User user = userService.getUserByPhone(dto.getPhone());
        if (user == null) {
            user = userService.createIndividualUserByPhone(dto.getPhone());
        }

        if (!user.isActive()) {
            return Result.error(403, "账号已停用");
        }

        String token = JwtUtil.generateToken(user.getId());
        String role = userService.getUserRoleNameById(user.getId());

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setRoleName(role);
        return Result.success(resp);
    }
}
