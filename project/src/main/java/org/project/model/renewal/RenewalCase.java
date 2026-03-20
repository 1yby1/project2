package org.project.model.renewal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("renewal_case")
public class RenewalCase {
    @TableId(value = "case_id", type = IdType.AUTO)
    private Long caseId;

    @TableField("case_no")
    private String caseNo;

    @TableField("unit_id")
    private Long unitId;

    /**
     * 状态：
     * 1-填写中
     * 2-待单位确认
     * 3-待管理员确认
     * 4-待缴费
     * 5-待财务审核
     * 6-待签章
     * 7-已完成
     * -1-已取消
     * -2-已拒绝
     */
    private Integer status;

    @TableField("expire_at")
    private LocalDateTime expireAt;

    @TableField("strategy_id")
    private Long strategyId;

    @TableField("original_amount")
    private BigDecimal originalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("final_amount")
    private BigDecimal finalAmount;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("reject_reason")
    private String rejectReason;
}
