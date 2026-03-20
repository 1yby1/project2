package org.project.model.Dto.CylinderRegisterDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 气瓶登记列表项 DTO
 */
@Data
public class CylinderRegisterListDto {

    private Long registerId;

    private Long contractId;

    private String contractNo;

    private String unitName;

    private String cylinderNo;

    private String rfidTag;

    private String cylinderTypeName;

    private String manufacturer;

    private BigDecimal designPressure;

    private BigDecimal volume;

    private LocalDate manufactureDate;

    private LocalDate lastInspectDate;

    private LocalDate nextInspectDate;

    /**
     * 状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存
     */
    private Integer cylinderStatus;

    private String cylinderStatusText;

    private String location;

    /**
     * 是否即将到期
     */
    private Boolean isInspectDueSoon;

    /**
     * 是否已过期
     */
    private Boolean isInspectOverdue;
}
