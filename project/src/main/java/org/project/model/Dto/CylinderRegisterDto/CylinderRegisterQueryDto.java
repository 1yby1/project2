package org.project.model.Dto.CylinderRegisterDto;

import lombok.Data;

/**
 * 气瓶登记查询条件 DTO
 */
@Data
public class CylinderRegisterQueryDto {

    /**
     * 合同ID（筛选某合同下的气瓶）
     */
    private Long contractId;

    /**
     * 单位ID（筛选某单位的气瓶）
     */
    private Long unitId;

    /**
     * 气瓶类型ID
     */
    private Long cylinderTypeId;

    /**
     * 气瓶编号（模糊查询）
     */
    private String cylinderNo;

    /**
     * RFID标签（精确查询）
     */
    private String rfidTag;

    /**
     * 气瓶状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存
     */
    private Integer cylinderStatus;


    private String inspectionStatus;
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
