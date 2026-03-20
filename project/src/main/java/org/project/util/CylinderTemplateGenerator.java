package org.project.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * 气瓶导入模板生成器
 */
@Component
public class CylinderTemplateGenerator {

    /**
     * 生成气瓶导入模板
     */
    public byte[] generateCylinderTemplate() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        // 创建说明sheet
        Sheet instructionSheet = workbook.createSheet("使用说明");
        createInstructionSheet(workbook, instructionSheet);
        
        // 创建数据sheet
        Sheet dataSheet = workbook.createSheet("气瓶数据");
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
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        
        int rowNum = 0;
        
        // 标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("气瓶批量导入模板使用说明");
        titleCell.setCellStyle(titleStyle);
        
        rowNum++; // 空行
        
        // 说明内容
        String[] instructions = {
            "1. 模板填写说明：",
            "   - 请在【气瓶数据】sheet中填写数据，不要修改表头",
            "   - 带*号的列为必填项",
            "   - 日期格式：yyyy-MM-dd（如：2026-01-12）",
            "",
            "2. 字段说明：",
            "   - 气瓶编号*：唯一标识气瓶的编号，不能重复",
            "   - RFID标签：气瓶上的RFID标签编号（可选）",
            "   - 制造日期：气瓶的制造日期（可选）",
            "   - 制造厂商：生产气瓶的厂商名称（可选）",
            "   - 设计压力(MPa)：气瓶的设计压力值（可选）",
            "   - 容积(L)：气瓶的容积大小（可选）",
            "   - 空瓶重量(kg)：气瓶的空瓶重量（可选）",
            "   - 最近检验日期：上一次检验的日期（可选）",
            "   - 下次检验日期：下一次应该检验的日期（可选）",
            "   - 位置：气瓶当前所在位置（可选）",
            "   - 备注：其他需要说明的信息（可选）",
            "",
            "3. 注意事项：",
            "   - 气瓶编号必须唯一，不能与系统中已有的重复",
            "   - 每次最多导入1000条记录",
            "   - 导入前请确保已创建对应的合同",
            "   - 如有错误，系统会返回详细的错误信息",
            "",
            "4. 示例数据：",
            "   请参考【气瓶数据】sheet中的示例行"
        };
        
        for (String instruction : instructions) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(instruction);
            cell.setCellStyle(contentStyle);
        }
        
        sheet.setColumnWidth(0, 18000);
    }

    /**
     * 创建数据sheet
     */
    private void createDataSheet(XSSFWorkbook workbook, Sheet sheet) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "气瓶编号*", "RFID标签", "制造日期", "制造厂商",
            "设计压力(MPa)", "容积(L)", "空瓶重量(kg)",
            "最近检验日期", "下次检验日期", "位置", "备注"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4500);
        }
        
        // 添加示例数据
        Row exampleRow = sheet.createRow(1);
        
        exampleRow.createCell(0).setCellValue("GYP202601120001");
        exampleRow.createCell(1).setCellValue("RFID0001");
        
        Cell manuDateCell = exampleRow.createCell(2);
        manuDateCell.setCellValue(LocalDate.now().minusYears(5));
        manuDateCell.setCellStyle(dateStyle);
        
        exampleRow.createCell(3).setCellValue("XX气瓶厂");
        
        Cell pressureCell = exampleRow.createCell(4);
        pressureCell.setCellValue(15.0);
        pressureCell.setCellStyle(numberStyle);
        
        Cell volumeCell = exampleRow.createCell(5);
        volumeCell.setCellValue(40.0);
        volumeCell.setCellStyle(numberStyle);
        
        Cell weightCell = exampleRow.createCell(6);
        weightCell.setCellValue(35.5);
        weightCell.setCellStyle(numberStyle);
        
        Cell lastInspectCell = exampleRow.createCell(7);
        lastInspectCell.setCellValue(LocalDate.now().minusYears(1));
        lastInspectCell.setCellStyle(dateStyle);
        
        Cell nextInspectCell = exampleRow.createCell(8);
        nextInspectCell.setCellValue(LocalDate.now().plusYears(2));
        nextInspectCell.setCellStyle(dateStyle);
        
        exampleRow.createCell(9).setCellValue("仓库A区");
        exampleRow.createCell(10).setCellValue("示例气瓶");
        
        // 冻结首行
        sheet.createFreezePane(0, 1);
    }

    private CellStyle createTitleStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createContentStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    private CellStyle createDateStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    private CellStyle createNumberStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.RIGHT);
        
        return style;
    }
}
