package org.project.model.renewal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("renewal_strategy_price")
public class RenewalStrategyPrice {
    @TableId(value = "price_id", type = IdType.AUTO)
    private Long priceId;

    @TableField("strategy_id")
    private Long strategyId;

    @TableField("cylinder_type_id")
    private Long cylinderTypeId;

    @TableField("min_qty")
    private Integer minQty;

    @TableField("max_qty")
    private Integer maxQty;

    @TableField("credit_level")
    private Integer creditLevel;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("discount_rate")
    private BigDecimal discountRate;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
