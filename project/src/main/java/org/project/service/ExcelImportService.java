package org.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Excel批量导入服务
 */
public interface ExcelImportService {

    /**
     * 批量导入合同
     *
     * @param file      Excel文件
     * @param creatorId 创建人ID
     * @return 导入结果（成功数、失败数、错误信息）
     */
    ImportResult importContracts(MultipartFile file, Long creatorId);

    /**
     * 下载合同导入模板
     *
     * @return Excel文件路径
     */
    String downloadTemplate();

    /**
     * 导入结果
     */
    class ImportResult {
        private int successCount;
        private int failureCount;
        private List<ErrorRow> errors;

        public ImportResult() {
        }

        public ImportResult(int successCount, int failureCount, List<ErrorRow> errors) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public List<ErrorRow> getErrors() {
            return errors;
        }

        public void setErrors(List<ErrorRow> errors) {
            this.errors = errors;
        }
    }

    /**
     * 错误行信息
     */
    class ErrorRow {
        private int rowNum;
        private String errorMsg;

        public ErrorRow() {
        }

        public ErrorRow(int rowNum, String errorMsg) {
            this.rowNum = rowNum;
            this.errorMsg = errorMsg;
        }

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}
