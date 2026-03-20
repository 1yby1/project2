package org.project.model.Dto.AuthDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneCodeSendDto {
    @NotBlank(message = "手机号不能为空")
    private String phone;

}

