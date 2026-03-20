package org.project.model.Dto.ContractDto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * 合同更新请求DTO
 */
@Data
public class ContractUpdateDto {

    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 生效日期（已生效合同不可修改）
     */
    private LocalDate startDate;

    /**
     * 到期日期（已生效合同不可修改）
     */
    private LocalDate endDate;

    /**
     * 气瓶明细列表（已生效合同不可修改）
     */
    private List<ContractCreateDto.CylinderLineDto> cylinderLines;

    /**
     * 产品明细列表（已生效合同不可修改）
     */
    private List<ContractCreateDto.ProductItemDto> productItems;

    /**
     * 备注（所有状态都可修改）
     */
    private String remark;
}
