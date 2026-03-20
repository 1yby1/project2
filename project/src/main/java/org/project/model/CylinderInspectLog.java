package org.project.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 气瓶检验记录实体
 */
@Data
@TableName("cylinder_inspect_log")
public class CylinderInspectLog {
    
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 气瓶登记ID
     */
    private Long registerId;

    /**
     * 气瓶编号
     */
    private String cylinderNo;

    /**
     * 检验日期
     */
    private LocalDate inspectDate;

    /**
     * 检验结果：1-合格 2-不合格 3-报废
     */
    private Integer inspectResult;

    /**
     * 检验员
     */
    private String inspector;

    /**
     * 检验机构
     */
    private String inspectOrg;

    /**
     * 下次检验日期
     */
    private LocalDate nextInspectDate;

    /**
     * 备注
     */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
