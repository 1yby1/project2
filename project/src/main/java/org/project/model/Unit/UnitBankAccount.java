package org.project.model.Unit;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("unit_bank_account")
public class UnitBankAccount {
    @TableId(type = IdType.AUTO)
    private Long accountId;

    private Long unitId;

    private String accountName;

    private String bankName;

    private String bankBranch;

    private String bankAccountNo;

    /**
     * 是否默认：1-是 0-否
     */
    private Integer isDefault;

    /**
     * 状态：1-启用 0-停用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
