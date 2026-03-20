package org.project.model.Dto.ContractDto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 合同查询条件DTO
 */
@Data
public class ContractQueryDto {

    /**
     * 单位名称（管理员可选，单位用户自动填充）
     */
    private String unitName;

    /**
     * 关键词（合同编号）
     */
    private String keyword;

    /**
     * 合同状态
     */
    private Integer contractStatus;

    /**
     * 开始日期范围 - 起始
     */
    private LocalDate startDateBegin;

    /**
     * 开始日期范围 - 结束
     */
    private LocalDate startDateEnd;

    /**
     * 结束日期范围 - 起始
     */
    private LocalDate endDateBegin;

    /**
     * 结束日期范围 - 结束
     */
    private LocalDate endDateEnd;

    /**
     * 页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10）
     */
    private Integer pageSize = 10;
}
