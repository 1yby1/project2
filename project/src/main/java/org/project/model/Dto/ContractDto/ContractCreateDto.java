package org.project.model.Dto.ContractDto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同创建请求DTO
 */
@Data
public class ContractCreateDto {

    /**
     * 单位ID（必填）
     */
    @NotNull(message = "单位ID不能为空")
    private Long unitId;

    /**
     * 合同编号（可选，不填则自动生成）
     */
    private String contractNo;

    /**
     * 生效日期
     */
    @NotNull(message = "生效日期不能为空")
    private LocalDate startDate;

    /**
     * 到期日期
     */
    @NotNull(message = "到期日期不能为空")
    private LocalDate endDate;

    /**
     * 气瓶明细列表
     */
    @NotEmpty(message = "气瓶明细不能为空")
    private List<CylinderLineDto> cylinderLines;

    /**
     * 产品明细列表（可选）
     */
    private List<ProductItemDto> productItems;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扫描件文件URL（可选）
     */
    private String scannedFileUrl;

    @Data
    public static class CylinderLineDto {
        @NotNull(message = "气瓶类型ID不能为空")
        private Long cylinderTypeId;

        @NotNull(message = "气瓶数量不能为空")
        @Min(value = 1, message = "气瓶数量至少为1")
        private Integer cylinderQty;

        @NotNull(message = "单价不能为空")
        @DecimalMin(value = "0.01", message = "单价必须大于0")
        private BigDecimal unitPrice;

        @NotNull(message = "年限不能为空")
        @DecimalMin(value = "0.01", message = "年限必须大于0")
        private BigDecimal years;

        /**
         * 折扣率（默认1.0，即无折扣）
         */
        private BigDecimal discountRate = BigDecimal.ONE;
    }

    @Data
    public static class ProductItemDto {
        @NotBlank(message = "产品编码不能为空")
        private String productCode;

        @NotBlank(message = "产品名称不能为空")
        private String productName;

        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为1")
        private Integer quantity;

        @NotNull(message = "单价不能为空")
        @DecimalMin(value = "0.01", message = "单价必须大于0")
        private BigDecimal unitPrice;
    }
}
