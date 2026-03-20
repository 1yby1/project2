package org.project.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.project.mapper.unitMapper.UnitMapper;
import org.project.model.Dto.ContractDto.ContractCreateDto;
import org.project.model.Unit.Unit;
import org.project.service.ContractNoService;
import org.project.service.ContractService;
import org.project.service.ExcelImportService;
import org.project.service.FileUploadService;
import org.project.util.ExcelTemplateGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportServiceImpl implements ExcelImportService {

    private final ContractService contractService;
    private final ContractNoService contractNoService;
    private final UnitMapper unitMapper;
    private final ExcelTemplateGenerator excelTemplateGenerator;
    private final FileUploadService fileUploadService;
    
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;
    
    @Value("${file.upload.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    @Override
    public ImportResult importContracts(MultipartFile file, Long creatorId) {
        List<ErrorRow> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            // 跳过标题行
            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue; // 跳过表头

                try {
                    // 解析每一行数据
                    ContractCreateDto dto = parseRowToDto(row, rowNum);

                    // 校验数据
                    validateDto(dto, rowNum);

                    Long contractId = contractService.createContract(dto, creatorId);

                    // 批量导入时直接置为已生效（状态码6）
                    contractService.updateContractStatus(contractId, 6);
                    successCount++;

                } catch (Exception e) {
                    failureCount++;
                    errors.add(new ErrorRow(rowNum, e.getMessage()));
                    log.error("第{}行导入失败: {}", rowNum, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("批量导入失败", e);
            errors.add(new ErrorRow(0, "文件解析失败：" + e.getMessage()));
        }

        return new ImportResult(successCount, failureCount, errors);
    }

    @Override
    public String downloadTemplate() {
        try {
            // 生成Excel模板字节数组
            byte[] templateBytes = excelTemplateGenerator.generateContractTemplate();
            
            // 保存为文件
            String datePath = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "template/" + datePath;
            java.nio.file.Path dirPath = java.nio.file.Paths.get(uploadBasePath, relativePath);
            
            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
            }
            
            String filename = "合同导入模板_" + System.currentTimeMillis() + ".xlsx";
            java.nio.file.Path filePath = dirPath.resolve(filename);
            java.nio.file.Files.write(filePath, templateBytes);
            
            // 返回URL
            String fileUrl = urlPrefix + "/" + relativePath + "/" + filename;
            log.info("模板生成成功：{}", fileUrl);
            return fileUrl;
            
        } catch (Exception e) {
            log.error("生成模板失败", e);
            throw new RuntimeException("生成模板失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析Excel行数据为DTO
     */
    private ContractCreateDto parseRowToDto(Row row, int rowNum) {
        ContractCreateDto dto = new ContractCreateDto();

        try {
            // 列索引
            int colIdx = 0;

            // 单位ID（必填）
            Cell unitIdCell = row.getCell(colIdx++);
            if (unitIdCell == null || unitIdCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("单位ID不能为空");
            }
            dto.setUnitId((long) unitIdCell.getNumericCellValue());

            // 合同编号（可选）
            Cell contractNoCell = row.getCell(colIdx++);
            if (contractNoCell != null && contractNoCell.getCellType() != CellType.BLANK) {
                dto.setContractNo(getCellStringValue(contractNoCell));
            }

            // 生效日期
            Cell startDateCell = row.getCell(colIdx++);
            if (startDateCell == null || startDateCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("生效日期不能为空");
            }
            dto.setStartDate(convertToLocalDate(startDateCell.getDateCellValue()));

            // 到期日期
            Cell endDateCell = row.getCell(colIdx++);
            if (endDateCell == null || endDateCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("到期日期不能为空");
            }
            dto.setEndDate(convertToLocalDate(endDateCell.getDateCellValue()));

            // 气瓶类型ID
            Cell cylinderTypeIdCell = row.getCell(colIdx++);
            if (cylinderTypeIdCell == null || cylinderTypeIdCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("气瓶类型ID不能为空");
            }
            Long cylinderTypeId = (long) cylinderTypeIdCell.getNumericCellValue();

            // 气瓶数量
            Cell cylinderQtyCell = row.getCell(colIdx++);
            if (cylinderQtyCell == null || cylinderQtyCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("气瓶数量不能为空");
            }
            Integer cylinderQty = (int) cylinderQtyCell.getNumericCellValue();

            // 单价
            Cell unitPriceCell = row.getCell(colIdx++);
            if (unitPriceCell == null || unitPriceCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("单价不能为空");
            }
            BigDecimal unitPrice = BigDecimal.valueOf(unitPriceCell.getNumericCellValue());

            // 年限
            Cell yearsCell = row.getCell(colIdx++);
            if (yearsCell == null || yearsCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("年限不能为空");
            }
            BigDecimal years = BigDecimal.valueOf(yearsCell.getNumericCellValue());

            // 折扣率（可选，默认1.0）
            Cell discountRateCell = row.getCell(colIdx++);
            BigDecimal discountRate = BigDecimal.ONE;
            if (discountRateCell != null && discountRateCell.getCellType() != CellType.BLANK) {
                discountRate = BigDecimal.valueOf(discountRateCell.getNumericCellValue());
            }

            // 备注（可选）
            Cell remarkCell = row.getCell(colIdx++);
            if (remarkCell != null && remarkCell.getCellType() != CellType.BLANK) {
                dto.setRemark(getCellStringValue(remarkCell));
            }

            // 构建气瓶明细
            ContractCreateDto.CylinderLineDto lineDto = new ContractCreateDto.CylinderLineDto();
            lineDto.setCylinderTypeId(cylinderTypeId);
            lineDto.setCylinderQty(cylinderQty);
            lineDto.setUnitPrice(unitPrice);
            lineDto.setYears(years);
            lineDto.setDiscountRate(discountRate);

            List<ContractCreateDto.CylinderLineDto> cylinderLines = new ArrayList<>();
            cylinderLines.add(lineDto);
            dto.setCylinderLines(cylinderLines);

        } catch (Exception e) {
            throw new IllegalArgumentException("第" + rowNum + "行数据格式错误：" + e.getMessage());
        }

        return dto;
    }

    /**
     * 校验DTO数据
     */
    private void validateDto(ContractCreateDto dto, int rowNum) {
        // 校验单位是否存在
        Unit unit = unitMapper.selectById(dto.getUnitId());
        if (unit == null) {
            throw new IllegalArgumentException("第" + rowNum + "行：单位ID不存在");
        }

        // 校验日期逻辑
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("第" + rowNum + "行：结束日期必须大于开始日期");
        }

        // 校验合同编号唯一性
        if (dto.getContractNo() != null && contractNoService.checkContractNoExists(dto.getContractNo())) {
            throw new IllegalArgumentException("第" + rowNum + "行：合同编号已存在");
        }

        // 校验气瓶明细
        if (dto.getCylinderLines() == null || dto.getCylinderLines().isEmpty()) {
            throw new IllegalArgumentException("第" + rowNum + "行：气瓶明细不能为空");
        }
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Date转LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
