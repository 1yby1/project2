package org.project.model.renewal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("renewal_case_contract")
public class RenewalCaseContract {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("case_id")
    private Long caseId;

    @TableField("original_contract_id")
    private Long originalContractId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
