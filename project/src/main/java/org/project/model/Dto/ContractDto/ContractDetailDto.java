package org.project.model.Dto.ContractDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同详情响应DTO
 */
@Data
public class ContractDetailDto {

    // ========== 基础信息 ==========
    private Long contractId;
    private String contractNo;
    private Integer contractType;
    private Integer contractStatus;
    private String contractStatusText;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long remainingDays;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========== 单位信息 ==========
    private Long unitId;
    private String unitName;
    private String unitAddress;
    private String unitPrincipalName;
    private String unitPrincipalPhone;
    private String socialCreditCode;

    // ========== 金额信息 ==========
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    // ========== 气瓶明细 ==========
    private List<CylinderLineDetailDto> cylinderLines;

    // ========== 产品明细 ==========
    private List<ProductItemDetailDto> productItems;

    // ========== 甲乙方快照 ==========
    private PartySnapshotDto partySnapshot;

    // ========== 文件信息 ==========
    private List<FileDetailDto> files;

    // ========== 续约链 ==========
    private List<RenewalChainDto> renewalChain;

    // ========== 编辑权限 ==========
    private Boolean isEditable;
    private Boolean isOnlyRemarkEditable;

    @Data
    public static class CylinderLineDetailDto {
        private Long lineId;
        private Long cylinderTypeId;
        private String cylinderTypeName;
        private Integer cylinderQty;
        private BigDecimal unitPrice;
        private BigDecimal years;
        private BigDecimal originalAmount;
        private BigDecimal discountRate;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
    }

    @Data
    public static class ProductItemDetailDto {
        private Long itemId;
        private String productCode;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;
    }

    @Data
    public static class PartySnapshotDto {
        // 甲方
        private String partyAUnitName;
        private String partyASocialCreditCode;
        private String partyAAddress;
        private String partyAPrincipalName;
        private String partyAPrincipalPhone;
        private String partyABankName;
        private String partyABankAccount;

        // 乙方
        private String partyBCompanyName;
        private String partyBPhone;
        private String partyBAddress;
        private String partyBBankName;
        private String partyBBankAccount;
    }

    @Data
    public static class FileDetailDto {
        private Long fileId;
        private Integer fileType;
        private String fileTypeName;
        private String fileName;
        private String fileUrl;
        private LocalDateTime uploadedAt;
    }

    @Data
    public static class RenewalChainDto {
        private Long contractId;
        private String contractNo;
        private Integer renewalCount;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer contractStatus;
        private String contractStatusText;
    }

    /**
     * 获取合同状态文本
     */
    /**
     * 合同状态说明：
     * 1 - 待确认：单位用户创建后，等待管理员审核
     * 2 - 已退回：管理员退回，需单位用户修改后重新提交
     * 3 - 已确认/待缴费：管理员确认通过，等待单位用户缴费
     * 4 - 待审核缴费：单位用户已提交缴费信息，等待管理员审核
     * 5 - 已生效：缴费审核通过，合同正式生效
     * 6 - 即将到期：合同即将到期（系统自动标记）
     * 7 - 已到期：合同已到期
     * 8 - 已作废：合同已终止/作废
     */
    public static String getContractStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 1 -> "待确认";
            case 2 -> "已退回";
            case 3 -> "已确认(待缴费)";
            case 4 -> "待审核缴费";
            case 5 -> "已生效";
            case 6 -> "即将到期";
            case 7 -> "已到期";
            case 8 -> "已作废";
            default -> "未知";
        };
    }

    /**
     * 获取文件类型文本
     */
    public static String getFileTypeText(Integer fileType) {
        if (fileType == null) return "未知";
        return switch (fileType) {
            case 1 -> "扫描件";
            case 2 -> "草稿PDF";
            case 3 -> "签章PDF";
            case 4 -> "证据链";
            default -> "未知";
        };
    }
}
