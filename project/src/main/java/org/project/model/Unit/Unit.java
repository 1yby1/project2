package org.project.model.Unit;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("unit")
public class Unit {
    @TableId(type = IdType.AUTO)
    private Long unitId;

    private String unitCode;

    private String unitName;

    private String socialCreditCode;

    private String regionCode;

    private String unitAddress;

    private String unitPrincipalName;

    private String unitPrincipalPhone;

    private String unitPrincipalIdCard;

    /**
     * 信用等级：1-优秀 2-良好 3-一般 4-差
     */
    private Integer creditLevel;

    /**
     * 状态：1-正常 0-停用 -1-注销
     */
    private Integer status;

    /**
     * 服务状态：1-正常 2-受限(到期) 3-冻结
     */
    private Integer serviceStatus;

    private LocalDate serviceValidTo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private String remark;

    @TableField(exist = false)
    private Integer contractCount;
}
