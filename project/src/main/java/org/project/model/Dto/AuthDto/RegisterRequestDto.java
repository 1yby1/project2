package org.project.model.Dto.AuthDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度不能少于6位,不能大于16位")
    private String password;

    @NotBlank(message="电话号不能为空")
    @Size(min=11,max=11,message="电话号码必须为11位")
    private String phone;

    @NotBlank(message="验证码不能为空")
    private String code;

}
