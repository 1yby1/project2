package org.project.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    @TableField("id_card")
    private String idCard;

    @TableField("real_name")
    private String realName;

    @TableField("unit_id")
    private Long unitId;

    @TableField("role_id")
    private Long roleId;

    @TableField(exist = false)
    private String unitName;
    /**
     * 状态：1-正常 0-停用 -1-注销
     */
    private Integer status;

    public boolean isActive() {
        return status == null || status == 1;
    }
}
