package org.project.model.Dto.AuthDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhonePasswordLoginDto {
    @NotBlank
    private String phone;
    @NotBlank
    private String password;

}
