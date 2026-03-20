package org.project.model.contract;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同气瓶登记追踪实体
 */
@Data
@TableName("contract_cylinder_register")
public class ContractCylinderRegister {
    
    @TableId(type = IdType.AUTO)
    private Long registerId;

    /**
     * 所属合同ID
     */
    private Long contractId;

    /**
     * 气瓶类型ID
     */
    private Long cylinderTypeId;

    /**
     * 气瓶编号（唯一）
     */
    private String cylinderNo;

    /**
     * RFID标签编号
     */
    private String rfidTag;

    /**
     * 制造日期
     */
    private LocalDate manufactureDate;

    /**
     * 制造厂商
     */
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

    /**
     * 最近检验日期
     */
    private LocalDate lastInspectDate;

    /**
     * 下次检验到期日
     */
    private LocalDate nextInspectDate;

    /**
     * 状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存
     */
    private Integer cylinderStatus;

    /**
     * 当前位置
     */
    private String location;

    /**
     * 备注
     */
    private String remark;

    /**
     * 登记人ID
     */
    private Long registeredBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registeredAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 气瓶类型名称（关联查询时填充）
     */
    @TableField(exist = false)
    private String cylinderTypeName;

    /**
     * 合同编号（关联查询时填充）
     */
    @TableField(exist = false)
    private String contractNo;

    /**
     * 所属单位名称（关联查询时填充）
     */
    @TableField(exist = false)
    private String unitName;

    /**
     * 登记人姓名（关联查询时填充）
     */
    @TableField(exist = false)
    private String registeredByName;

    /**
     * 是否即将到期（距下次检验不足30天）
     */
    public boolean isInspectDueSoon() {
        if (nextInspectDate == null) {
            return false;
        }
        return nextInspectDate.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * 是否已过期
     */
    public boolean isInspectOverdue() {
        if (nextInspectDate == null) {
            return false;
        }
        return nextInspectDate.isBefore(LocalDate.now());
    }
}
