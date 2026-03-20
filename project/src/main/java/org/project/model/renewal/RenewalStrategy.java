package org.project.model.renewal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("renewal_strategy")
public class RenewalStrategy {
    @TableId(value = "strategy_id", type = IdType.AUTO)
    private Long strategyId;

    @TableField("strategy_name")
    private String strategyName;

    @TableField("renewal_years")
    private BigDecimal renewalYears;

    @TableField("is_recommended")
    private Integer isRecommended;

    private Integer status;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
