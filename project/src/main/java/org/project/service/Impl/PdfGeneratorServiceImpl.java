package org.project.service.Impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;
import org.project.model.Dto.ContractDto.ContractDetailDto;
import org.project.model.Dto.ContractDto.ContractInitDto;
import org.project.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * PDF生成服务实现
 */
@Slf4j
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;

    @Value("${file.upload.url-prefix:http://localhost:8081/files}")
    private String urlPrefix;

    @Override
    public String generateContractPdf(ContractDetailDto contractDetail) throws Exception {
        // 创建PDF存储路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "contract/pdf/" + datePath;
        Path dirPath = Paths.get(uploadBasePath, relativePath);

        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 生成PDF文件名
        String filename = "contract_" + contractDetail.getContractId() + "_" +
                UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        Path pdfPath = dirPath.resolve(filename);

        // 创建PDF文档
        PdfWriter writer = new PdfWriter(pdfPath.toFile());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // 加载中文字体
        PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
        document.setFont(font);

        // 如果是草稿状态，添加水印
        if (contractDetail.getContractStatus() == 0) {
            addWatermark(pdfDoc, font, "草稿");
        }

        // 添加标题
        Paragraph title = new Paragraph("气瓶定期检验合同")
                .setFont(font)
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // 添加合同编号和日期
        Paragraph info = new Paragraph(
                "合同编号：" + contractDetail.getContractNo() + "          " +
                        "签订日期：" + (LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        )
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(15);
        document.add(info);

        // 添加甲方信息
        document.add(createSectionTitle("甲方（检验单位）", font));
        document.add(createInfoTable(font, new String[][]{
                {"单位名称", contractDetail.getUnitName()},
                {"统一社会信用代码", contractDetail.getSocialCreditCode()},
                {"联系人", contractDetail.getUnitPrincipalName()},
                {"联系电话", contractDetail.getUnitPrincipalPhone()},
                {"单位地址", contractDetail.getUnitAddress()}
        }));

        ContractInitDto.PartyBInfo partyBInfo = new ContractInitDto.PartyBInfo();
        partyBInfo.setCompanyName("瓶安保技术有限公司");
        partyBInfo.setSocialCreditCode("91110000MA00000000");
        partyBInfo.setAddress("北京市西城区长椿街1号");
        partyBInfo.setContactName("李经理");
        partyBInfo.setContactPhone("010-65262000");
        partyBInfo.setBankName("中国银行北京分行");
        partyBInfo.setBankAccount("123456789012345");


        // 添加乙方信息
        document.add(createSectionTitle("乙方（送检单位）", font));
        document.add(createInfoTable(font, new String[][]{
                {"单位名称", partyBInfo.getCompanyName()},
                {"统一社会信用代码", partyBInfo.getSocialCreditCode()},
                {"联系人", partyBInfo.getContactName()},
                {"联系电话", partyBInfo.getContactPhone()},
                {"单位地址", partyBInfo.getAddress()}
        }));

        // 添加合同明细
        document.add(createSectionTitle("合同明细", font));
        if (contractDetail.getCylinderLines() != null && !contractDetail.getCylinderLines().isEmpty()) {
            // 创建明细表格
            float[] columnWidths = {1, 2, 1, 1, 1, 1.5f};
            Table itemTable = new Table(UnitValue.createPercentArray(columnWidths))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            // 表头
            String[] headers = {"序号", "气瓶类型", "数量", "单价(元)", "金额(元)", "备注"};
            for (String header : headers) {
                itemTable.addCell(createTableHeaderCell(header, font));
            }

            // 数据行
            int index = 1;
            for (ContractDetailDto.CylinderLineDetailDto item : contractDetail.getCylinderLines()) {
                itemTable.addCell(createTableCell(String.valueOf(index++), font));
                itemTable.addCell(createTableCell(item.getCylinderTypeName(), font));
                itemTable.addCell(createTableCell(String.valueOf(item.getCylinderQty()), font));
                itemTable.addCell(createTableCell(item.getUnitPrice().toString(), font));
                itemTable.addCell(createTableCell(item.getFinalAmount().toString(), font));
                itemTable.addCell(createTableCell(contractDetail.getRemark() != null ? contractDetail.getRemark() : "", font));
            }

            document.add(itemTable);
        }

        // 添加合同金额
        document.add(new Paragraph("合同总金额：" + contractDetail.getFinalAmount() + " 元")
                .setFont(font)
                .setFontSize(12)
                .setBold()
                .setMarginBottom(15));

        // 添加收款信息
        if (contractDetail.getPartySnapshot() != null) {
            document.add(createSectionTitle("收款账户信息", font));
            document.add(createInfoTable(font, new String[][]{
                    {"开户银行", contractDetail.getPartySnapshot().getPartyBBankName()},
                    {"银行账号", contractDetail.getPartySnapshot().getPartyBBankAccount()},
                    {"开户名称", contractDetail.getPartySnapshot().getPartyBCompanyName()}
            }));
        }

        // 添加合同状态
        document.add(createSectionTitle("合同状态", font));
        document.add(new Paragraph("当前状态：" + getContractStatusText(contractDetail.getContractStatus()))
                .setFont(font)
                .setFontSize(10)
                .setMarginBottom(5));

        if (contractDetail.getRemark() != null && !contractDetail.getRemark().isEmpty()) {
            document.add(new Paragraph("备注：" + contractDetail.getRemark())
                    .setFont(font)
                    .setFontSize(10)
                    .setMarginBottom(15));
        }

        // 添加签字栏
        document.add(createSignatureSection(font));

        // 添加页脚
        addFooter(document, font);

        document.close();

        // 返回URL
        String fileUrl = urlPrefix + "/" + relativePath + "/" + filename;
        log.info("PDF生成成功：{}", fileUrl);
        return fileUrl;
    }

    /**
     * 创建章节标题
     */
    private Paragraph createSectionTitle(String title, PdfFont font) {
        return new Paragraph(title)
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(5)
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(5);
    }

    /**
     * 创建信息表格
     */
    private Table createInfoTable(PdfFont font, String[][] data) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        for (String[] row : data) {
            table.addCell(createTableCell(row[0], font, true));
            table.addCell(createTableCell(row[1] != null ? row[1] : "", font, false));
        }

        return table;
    }

    /**
     * 创建表格单元格
     */
    private Cell createTableCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(10))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /**
     * 创建表格单元格（带粗体选项）
     */
    private Cell createTableCell(String content, PdfFont font, boolean bold) {
        Paragraph p = new Paragraph(content).setFont(font).setFontSize(10);
        if (bold) {
            p.setBold();
        }
        return new Cell()
                .add(p)
                .setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /**
     * 创建表头单元格
     */
    private Cell createTableHeaderCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(10).setBold())
                .setPadding(5)
                .setBackgroundColor(new DeviceRgb(220, 220, 220))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /**
     * 创建签字栏
     */
    private Table createSignatureSection(PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(30);

        Cell partyACell = new Cell()
                .add(new Paragraph("甲方（盖章）：\n\n\n日期：    年   月   日")
                        .setFont(font)
                        .setFontSize(10))
                .setPadding(10)
                .setBorder(Border.NO_BORDER);

        Cell partyBCell = new Cell()
                .add(new Paragraph("乙方（盖章）：\n\n\n日期：    年   月   日")
                        .setFont(font)
                        .setFontSize(10))
                .setPadding(10)
                .setBorder(Border.NO_BORDER);

        table.addCell(partyACell);
        table.addCell(partyBCell);

        return table;
    }

    /**
     * 添加水印
     */
    private void addWatermark(PdfDocument pdfDoc, PdfFont font, String watermarkText) {
        int numberOfPages = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(i).newContentStreamBefore(),
                    pdfDoc.getPage(i).getResources(), pdfDoc);

            PdfExtGState gs = new PdfExtGState().setFillOpacity(0.2f);
            canvas.saveState();
            canvas.setExtGState(gs);
            canvas.beginText();
            canvas.setFontAndSize(font, 60);
            canvas.setColor(ColorConstants.GRAY, true);
            canvas.moveText(200, 400);
            canvas.showText(watermarkText);
            canvas.endText();
            canvas.restoreState();
        }
    }

    /**
     * 添加页脚
     */
    private void addFooter(Document document, PdfFont font) {
        document.add(new Paragraph("---------- 本合同一式两份，甲乙双方各执一份，具有同等法律效力 ----------")
                .setFont(font)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setFontColor(ColorConstants.GRAY));
    }

    /**
     * 获取合同状态文本
     */
    private String getContractStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "待确认";
            case 2 -> "已退回";
            case 3 -> "待缴费";
            case 4 -> "待审核";
            case 5 -> "待签署";
            case 6 -> "执行中";
            case 7 -> "已完成";
            case 8 -> "已终止";
            default -> "未知";
        };
    }
}
