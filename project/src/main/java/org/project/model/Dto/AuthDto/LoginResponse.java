package org.project.model.Dto.AuthDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private Long unitId;
    private String username;
    private String roleName;
    private String unitName;
    // 若需要可以添加更多字段，例如邮箱、权限列表等
}

