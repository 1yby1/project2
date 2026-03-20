package org.project.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Excel模板生成工具类
 */
@Component
public class ExcelTemplateGenerator {

    /**
     * 生成合同导入模板
     */
    public byte[] generateContractTemplate() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        // 创建说明sheet
        Sheet instructionSheet = workbook.createSheet("使用说明");
        createInstructionSheet(workbook, instructionSheet);
        
        // 创建数据sheet
        Sheet dataSheet = workbook.createSheet("合同数据");
        createDataSheet(workbook, dataSheet);
        
        // 输出为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }

    /**
     * 创建使用说明sheet
     */
    private void createInstructionSheet(XSSFWorkbook workbook, Sheet sheet) {
        // 创建样式
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        
        int rowNum = 0;
        
        // 标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("合同批量导入模板使用说明");
        titleCell.setCellStyle(titleStyle);
        
        rowNum++; // 空行
        
        // 说明内容
        String[] instructions = {
            "1. 模板填写说明：",
            "   - 请在【合同数据】sheet中填写数据，不要修改表头",
            "   - 带*号的列为必填项",
            "   - 日期格式：yyyy-MM-dd（如：2026-01-12）",
            "",
            "2. 字段说明：",
            "   - 单位ID*：必须是系统中已存在的单位ID",
            "   - 合同编号：可选，如不填写系统将自动生成",
            "   - 生效日期*：合同生效日期",
            "   - 到期日期*：合同到期日期，必须晚于生效日期",
            "   - 气瓶类型ID*：必须是系统中已存在的气瓶类型ID",
            "   - 气瓶数量*：正整数",
            "   - 单价*：单个气瓶的服务单价（元）",
            "   - 年限*：合同年限",
            "   - 折扣率：0-1之间的小数，默认1.0（无折扣）",
            "   - 备注：可选填写",
            "",
            "3. 注意事项：",
            "   - 导入前请确保单位和气瓶类型已在系统中创建",
            "   - 每次最多导入1000条记录",
            "   - 如有错误，系统会返回详细的错误信息",
            "   - 导入成功的数据会自动创建为草稿状态的合同",
            "",
            "4. 示例数据：",
            "   请参考【合同数据】sheet中的示例行"
        };
        
        for (String instruction : instructions) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(instruction);
            cell.setCellStyle(contentStyle);
        }
        
        // 设置列宽
        sheet.setColumnWidth(0, 15000);
    }

    /**
     * 创建数据sheet
     */
    private void createDataSheet(XSSFWorkbook workbook, Sheet sheet) {
        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "单位ID*", "合同编号", "生效日期*", "到期日期*", 
            "气瓶类型ID*", "气瓶数量*", "单价(元)*", "年限*", 
            "折扣率", "备注"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            
            // 设置列宽
            sheet.setColumnWidth(i, 4000);
        }
        
        // 添加示例数据
        Row exampleRow = sheet.createRow(1);
        
        // 单位ID
        Cell unitIdCell = exampleRow.createCell(0);
        unitIdCell.setCellValue(1);
        unitIdCell.setCellStyle(numberStyle);
        
        // 合同编号
        Cell contractNoCell = exampleRow.createCell(1);
        contractNoCell.setCellValue("HT202601120001");
        
        // 生效日期
        Cell startDateCell = exampleRow.createCell(2);
        startDateCell.setCellValue(LocalDate.now());
        startDateCell.setCellStyle(dateStyle);
        
        // 到期日期
        Cell endDateCell = exampleRow.createCell(3);
        endDateCell.setCellValue(LocalDate.now().plusYears(3));
        endDateCell.setCellStyle(dateStyle);
        
        // 气瓶类型ID
        Cell cylinderTypeIdCell = exampleRow.createCell(4);
        cylinderTypeIdCell.setCellValue(1);
        cylinderTypeIdCell.setCellStyle(numberStyle);
        
        // 气瓶数量
        Cell cylinderQtyCell = exampleRow.createCell(5);
        cylinderQtyCell.setCellValue(100);
        cylinderQtyCell.setCellStyle(numberStyle);
        
        // 单价
        Cell unitPriceCell = exampleRow.createCell(6);
        unitPriceCell.setCellValue(50.00);
        unitPriceCell.setCellStyle(numberStyle);
        
        // 年限
        Cell yearsCell = exampleRow.createCell(7);
        yearsCell.setCellValue(3);
        yearsCell.setCellStyle(numberStyle);
        
        // 折扣率
        Cell discountRateCell = exampleRow.createCell(8);
        discountRateCell.setCellValue(1.0);
        discountRateCell.setCellStyle(numberStyle);
        
        // 备注
        Cell remarkCell = exampleRow.createCell(9);
        remarkCell.setCellValue("示例合同");
        
        // 冻结首行
        sheet.createFreezePane(0, 1);
    }

    /**
     * 创建标题样式
     */
    private CellStyle createTitleStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    /**
     * 创建内容样式
     */
    private CellStyle createContentStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        // 背景色
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    /**
     * 创建日期样式
     */
    private CellStyle createDateStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        
        // 边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    /**
     * 创建数字样式
     */
    private CellStyle createNumberStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.RIGHT);
        
        return style;
    }
}
