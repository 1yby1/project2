package org.project.model.Dto.ContractDto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 合同终止请求DTO
 */
@Data
public class ContractTerminateDto {

    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @NotBlank(message = "终止原因不能为空")
    private String terminateReason;
}
