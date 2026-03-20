package org.project.model.Dto.ContractDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同列表项DTO
 */
@Data
public class ContractListItemDto {

    private Long contractId;

    private String contractNo;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 签约期限
     */
    private String contractPeriod;

    /**
     * 气瓶类型列表，逗号分隔
     */
    private String cylinderTypes;
    /**
     * 气瓶总数量
     */
    private Integer cylinderQty;
    /**
     * 合同金额
     */
    private BigDecimal finalAmount;

    /**
     * 合同状态
     */
    private Integer contractStatus;

    /**
     * 合同状态文本
     */
    private String contractStatusText;

    /**
     * 剩余天数
     */
    private Long remainingDays;

    /**
     * 生效日期
     */
    private LocalDate startDate;

    /**
     * 到期日期
     */
    private LocalDate endDate;

    /**
     * 是否可编辑
     */
    private Boolean isEditable;
}
