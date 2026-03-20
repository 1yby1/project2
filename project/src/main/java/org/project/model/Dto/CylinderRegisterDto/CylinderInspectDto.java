package org.project.model.Dto.CylinderRegisterDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 气瓶检验记录 DTO
 */
@Data
public class CylinderInspectDto {

    @NotNull(message = "气瓶登记ID不能为空")
    private Long registerId;

    @NotBlank(message = "气瓶编号不能为空")
    private String cylinderNo;

    @NotNull(message = "检验日期不能为空")
    private LocalDate inspectDate;

    @NotNull(message = "检验结果不能为空")
    private Integer inspectResult;

    private String inspector;

    private String inspectOrg;

    private LocalDate nextInspectDate;

    private String remark;
}
