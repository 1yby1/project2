package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("contract_cylinder_line")
public class ContractCylinderLine {
    @TableId(type = IdType.AUTO)
    private Long lineId;

    private Long contractId;

    private Long cylinderTypeId;

    private Integer cylinderQty;

    /**
     * 单价(元/瓶/年)
     */
    private BigDecimal unitPrice;

    /**
     * 年限
     */
    private BigDecimal years;

    private BigDecimal originalAmount;

    private BigDecimal discountRate;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 气瓶类型名称（非表字段，用于展示）
     */
    @TableField(exist = false)
    private String cylinderTypeName;
}
