package org.project.model.Dto.CylinderRegisterDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 气瓶状态更新 DTO
 */
@Data
public class CylinderStatusUpdateDto {

    @NotNull(message = "气瓶登记ID不能为空")
    private Long registerId;

    @NotNull(message = "状态不能为空")
    private Integer cylinderStatus;

    private String remark;
}
