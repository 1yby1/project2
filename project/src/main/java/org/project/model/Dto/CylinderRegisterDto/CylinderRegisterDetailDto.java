package org.project.model.Dto.CylinderRegisterDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 气瓶登记详情 DTO
 */
@Data
public class CylinderRegisterDetailDto {

    private Long registerId;

    private Long contractId;

    private String contractNo;

    private String unitName;

    private Long cylinderTypeId;

    private String cylinderTypeName;

    private String cylinderNo;

    private String rfidTag;

    private LocalDate manufactureDate;

    private String manufacturer;

    private BigDecimal designPressure;

    private BigDecimal volume;

    private BigDecimal weightEmpty;

    private LocalDate lastInspectDate;

    private LocalDate nextInspectDate;

    private Integer cylinderStatus;

    private String cylinderStatusText;

    private String location;

    private String remark;

    private String registeredByName;

    private LocalDateTime registeredAt;

    /**
     * 是否即将到期
     */
    private Boolean isInspectDueSoon;

    /**
     * 是否已过期
     */
    private Boolean isInspectOverdue;

    /**
     * 检验历史记录
     */
    private List<InspectLogDto> inspectLogs;

    @Data
    public static class InspectLogDto {
        private Long logId;
        private LocalDate inspectDate;
        private Integer inspectResult;
        private String inspectResultText;
        private String inspector;
        private String inspectOrg;
        private LocalDate nextInspectDate;
        private String remark;
    }

    public static String getCylinderStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 1 -> "正常使用";
            case 2 -> "待检验";
            case 3 -> "已过期";
            case 4 -> "已报废";
            case 5 -> "封存";
            default -> "未知";
        };
    }

    public static String getInspectResultText(Integer result) {
        if (result == null) return "";
        return switch (result) {
            case 1 -> "合格";
            case 2 -> "不合格";
            case 3 -> "报废";
            default -> "未知";
        };
    }
}
