package org.project.model.Unit;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("unit_cylinder_type")
public class UnitCylinderType {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long unitId;

    private Long cylinderTypeId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 气瓶类型名称（关联查询时填充）
     */
    @TableField(exist = false)
    private String cylinderTypeName;
}
