package org.project.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cylinder_type_dict")
public class CylinderTypeDict {
    @TableId(type = IdType.AUTO)
    private Long cylinderTypeId;

    private String code;

    private String name;

    /**
     * 状态：1-启用 0-停用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
