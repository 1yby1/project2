package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contract_party_snapshot")
public class ContractPartySnapshot {
    @TableId(type = IdType.AUTO)
    private Long snapshotId;

    private Long contractId;

    // 甲方（单位）信息
    private String partyAUnitName;
    private String partyASocialCreditCode;
    private String partyAAddress;
    private String partyAPrincipalName;
    private String partyAPrincipalPhone;
    private String partyAPrincipalIdCard;
    private String partyABankName;
    private String partyABankAccount;
    private String partyABankBranch;

    // 乙方（瓶安保）信息
    private String partyBCompanyName;
    private String partyBPhone;
    private String partyBAddress;
    private String partyBBankName;
    private String partyBBankAccount;
    private String partyBBankBranch;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
