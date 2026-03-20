package org.project.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.project.model.Dto.CylinderRegisterDto.CylinderRegisterDto;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 气瓶Excel导入工具类
 */
@Component
public class CylinderExcelImporter {

    /**
     * 从Excel导入气瓶数据
     * 
     * @param inputStream Excel文件输入流
     * @param contractId 合同ID
     * @param cylinderTypeId 气瓶类型ID
     * @return 气瓶DTO列表
     */
    public ImportResult importFromExcel(InputStream inputStream, Long contractId, Long cylinderTypeId) {
        List<CylinderRegisterDto> cylinders = new ArrayList<>();
        List<ErrorRow> errors = new ArrayList<>();
        int successCount = 0;
        int rowNum = 0;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue; // 跳过表头

                try {
                    CylinderRegisterDto dto = parseRow(row, rowNum, contractId, cylinderTypeId);
                    cylinders.add(dto);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new ErrorRow(rowNum, e.getMessage()));
                }
            }

        } catch (Exception e) {
            errors.add(new ErrorRow(0, "文件解析失败：" + e.getMessage()));
        }

        return new ImportResult(cylinders, successCount, errors.size(), errors);
    }

    /**
     * 解析Excel行数据
     */
    private CylinderRegisterDto parseRow(Row row, int rowNum, Long contractId, Long cylinderTypeId) {
        CylinderRegisterDto dto = new CylinderRegisterDto();
        dto.setContractId(contractId);
        dto.setCylinderTypeId(cylinderTypeId);

        int colIdx = 0;

        // 气瓶编号（必填）
        Cell cylinderNoCell = row.getCell(colIdx++);
        if (cylinderNoCell == null || cylinderNoCell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("气瓶编号不能为空");
        }
        dto.setCylinderNo(getCellStringValue(cylinderNoCell).trim());

        // RFID标签（可选）
        Cell rfidCell = row.getCell(colIdx++);
        if (rfidCell != null && rfidCell.getCellType() != CellType.BLANK) {
            dto.setRfidTag(getCellStringValue(rfidCell).trim());
        }

        // 制造日期（可选）
        Cell manufactureDateCell = row.getCell(colIdx++);
        if (manufactureDateCell != null && manufactureDateCell.getCellType() != CellType.BLANK) {
            try {
                dto.setManufactureDate(convertToLocalDate(manufactureDateCell.getDateCellValue()));
            } catch (Exception e) {
                // 如果不是日期格式，尝试解析为字符串
                String dateStr = getCellStringValue(manufactureDateCell);
                if (!dateStr.isEmpty()) {
                    try {
                        dto.setManufactureDate(LocalDate.parse(dateStr));
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("制造日期格式错误");
                    }
                }
            }
        }

        // 制造厂商（可选）
        Cell manufacturerCell = row.getCell(colIdx++);
        if (manufacturerCell != null && manufacturerCell.getCellType() != CellType.BLANK) {
            dto.setManufacturer(getCellStringValue(manufacturerCell).trim());
        }

        // 设计压力（可选）
        Cell designPressureCell = row.getCell(colIdx++);
        if (designPressureCell != null && designPressureCell.getCellType() != CellType.BLANK) {
            dto.setDesignPressure(BigDecimal.valueOf(getNumericValue(designPressureCell)));
        }

        // 容积（可选）
        Cell volumeCell = row.getCell(colIdx++);
        if (volumeCell != null && volumeCell.getCellType() != CellType.BLANK) {
            dto.setVolume(BigDecimal.valueOf(getNumericValue(volumeCell)));
        }

        // 空瓶重量（可选）
        Cell weightEmptyCell = row.getCell(colIdx++);
        if (weightEmptyCell != null && weightEmptyCell.getCellType() != CellType.BLANK) {
            dto.setWeightEmpty(BigDecimal.valueOf(getNumericValue(weightEmptyCell)));
        }

        // 最近检验日期（可选）
        Cell lastInspectDateCell = row.getCell(colIdx++);
        if (lastInspectDateCell != null && lastInspectDateCell.getCellType() != CellType.BLANK) {
            try {
                dto.setLastInspectDate(convertToLocalDate(lastInspectDateCell.getDateCellValue()));
            } catch (Exception e) {
                String dateStr = getCellStringValue(lastInspectDateCell);
                if (!dateStr.isEmpty()) {
                    dto.setLastInspectDate(LocalDate.parse(dateStr));
                }
            }
        }

        // 下次检验日期（可选）
        Cell nextInspectDateCell = row.getCell(colIdx++);
        if (nextInspectDateCell != null && nextInspectDateCell.getCellType() != CellType.BLANK) {
            try {
                dto.setNextInspectDate(convertToLocalDate(nextInspectDateCell.getDateCellValue()));
            } catch (Exception e) {
                String dateStr = getCellStringValue(nextInspectDateCell);
                if (!dateStr.isEmpty()) {
                    dto.setNextInspectDate(LocalDate.parse(dateStr));
                }
            }
        }

        // 位置（可选）
        Cell locationCell = row.getCell(colIdx++);
        if (locationCell != null && locationCell.getCellType() != CellType.BLANK) {
            dto.setLocation(getCellStringValue(locationCell).trim());
        }

        // 备注（可选）
        Cell remarkCell = row.getCell(colIdx++);
        if (remarkCell != null && remarkCell.getCellType() != CellType.BLANK) {
            dto.setRemark(getCellStringValue(remarkCell).trim());
        }

        // 默认状态为正常使用
        dto.setCylinderStatus(1);

        return dto;
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                // 如果是数字，转为整数字符串（去掉小数点）
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * 获取数值
     */
    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Date转LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 导入结果类
     */
    public static class ImportResult {
        private final List<CylinderRegisterDto> cylinders;
        private final int successCount;
        private final int failureCount;
        private final List<ErrorRow> errors;

        public ImportResult(List<CylinderRegisterDto> cylinders, int successCount, int failureCount, List<ErrorRow> errors) {
            this.cylinders = cylinders;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }

        public List<CylinderRegisterDto> getCylinders() {
            return cylinders;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public List<ErrorRow> getErrors() {
            return errors;
        }
    }

    /**
     * 错误行信息
     */
    public static class ErrorRow {
        private final int rowNum;
        private final String errorMsg;

        public ErrorRow(int rowNum, String errorMsg) {
            this.rowNum = rowNum;
            this.errorMsg = errorMsg;
        }

        public int getRowNum() {
            return rowNum;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
