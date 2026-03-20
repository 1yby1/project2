package org.project.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("unit_personnel")
public class UnitPersonnel {
    @TableId(type = IdType.AUTO, value = "personnel_id")
    private Long personnelId;

    @TableField("unit_id")
    private Long unitId;

    @TableField("person_name")
    private String personName;

    private String phone;

    @TableField("id_card")
    private String idCard;

    private String department;

    @TableField("role_type")
    private Integer roleType;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String unitName;

    // 添加name字段的getter/setter，用于兼容前端
    public String getName() {
        return this.personName;
    }

    public void setName(String name) {
        this.personName = name;
    }

    // 添加role字段的setter，用于兼容前端角色字符串到roleType的转换
    public void setRole(String role) {
        if (role != null) {
            switch (role) {
                case "admin":
                case "管理员":
                    this.roleType = 1;
                    break;
                case "user":
                case "操作员":
                    this.roleType = 2;
                    break;
                default:
                    this.roleType = 2; // 默认操作员
            }
        }
    }

    // 角色名称转换
    public String getRole() {
        switch (roleType) {
            case 1: return "单位管理员";
            case 2: return "操作员";
            case 3: return "只读查看";
            default: return "未知";
        }
    }

    // 状态文本转换
    public String getStatusText() {
        return status == 1 ? "启用" : "禁用";
    }
}