package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("contract_item")
public class ContractItem {
    @TableId(type = IdType.AUTO)
    private Long itemId;

    private Long contractId;

    private String productCode;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
