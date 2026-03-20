package org.project.model.Dto.CylinderRegisterDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 气瓶登记 DTO
 */
@Data
public class CylinderRegisterDto {

    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @NotNull(message = "气瓶类型ID不能为空")
    private Long cylinderTypeId;

    @NotBlank(message = "气瓶编号不能为空")
    private String cylinderNo;

    private String rfidTag;

    private LocalDate manufactureDate;

    private String manufacturer;

    /**
     * 设计压力(MPa)
     */
    private BigDecimal designPressure;

    /**
     * 容积(L)
     */
    private BigDecimal volume;

    /**
     * 空瓶重量(kg)
     */
    private BigDecimal weightEmpty;

    private LocalDate lastInspectDate;

    private LocalDate nextInspectDate;

    /**
     * 状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存
     */
    private Integer cylinderStatus = 1;

    private String location;

    private String remark;
}
