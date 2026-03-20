package org.project.model.Dto.ContractDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 合同审核流程相关DTO
 */
public class ContractAuditDto {

    /**
     * 管理员退回合同请求
     */
    @Data
    public static class RejectDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;

        @NotBlank(message = "退回原因不能为空")
        @JsonProperty("reject_reason")
        private String rejectReason;
    }

    /**
     * 管理员确认合同请求
     */
    @Data
    public static class ConfirmDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;

        /** 确认备注（可选） */
        private String remark;
    }

    /**
     * 用户提交缴费信息请求
     */
    @Data
    public static class PaymentSubmitDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;

        /** 缴费方式：1-银行转账 2-在线支付 3-现金 */
        @JsonProperty("payment_method")
        private Integer paymentMethod;

        /** 缴费金额 */
        @JsonProperty("payment_amount")
        private java.math.BigDecimal paymentAmount;

        /** 缴费凭证URL */
        @JsonProperty("payment_voucher_url")
        private String paymentVoucherUrl;

        /** 缴费备注 */
        private String remark;
    }

    /**
     * 管理员审核缴费请求
     */
    @Data
    public static class PaymentAuditDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;

        /** 审核结果：true-通过 false-不通过 */
        @NotNull(message = "审核结果不能为空")
        private Boolean approved;

        /** 审核备注（不通过时必填原因） */
        @JsonProperty("audit_remark")
        private String auditRemark;
    }

    /**
     * 单位用户重新提交合同请求（被退回后修改重新提交）
     */
    @Data
    public static class ResubmitDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;
    }

    @Data
    public static class SignDto {
        @NotNull(message = "合同ID不能为空")
        @JsonProperty("contract_id")
        private Long contractId;
    }
}
