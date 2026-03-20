package org.project.model.contract;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract {
    @TableId(type = IdType.AUTO)
    private Long contractId;

    private String contractNo;

    private Long unitId;

    /**
     * 类型：1-初始合同 2-续约合同
     */
    private Integer contractType;

    private Long parentContractId;

    private Integer renewalCount;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate renewalStartDate;

    private BigDecimal originalAmount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    /**
     * 合同状态：
     * 1-草稿/待确认
     * 2-被退回
     * 3-已确认(待缴费)
     * 4-缴费审核中
     * 5-待签章
     * 6-已生效
     * 7-即将到期
     * 8-已到期
     * 9-已作废
     * -1-已拒绝
     */
    private Integer contractStatus;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 关联查询字段（来自 unit 表，不落库）
     */
    @TableField(exist = false)
    private String unitName;

    @TableField(exist = false)
    private String unitPrincipalName;

    @TableField(exist = false)
    private String unitAddress;

    @TableField(exist = false)
    private String unitPrincipalPhone;

    @TableField(exist = false)
    private String socialCreditCode;

    /**
     * 经营气瓶类型（逗号/顿号分隔），列表查询时通过聚合填充
     */
    @TableField(exist = false)
    private String cylinderTypes;
    /**
     * 经营气瓶总数量（非持久化字段）
     */
    @TableField(exist = false)
    private Integer cylinderQty;
    /**
     * 计算剩余天数（非持久化字段）
     */
    public Long getRemainingDays() {
        if (endDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * 是否可编辑（非持久化字段）
     */
    public boolean isEditable() {
        // 已作废合同不可编辑
        if (contractStatus != null && contractStatus == 8) {
            return false;
        }
        return true;
    }

    /**
     * 是否只能编辑备注（非持久化字段）
     */
    public boolean isOnlyRemarkEditable() {
        // 已生效合同只能编辑备注
        return contractStatus != null && contractStatus == 5;
    }
}