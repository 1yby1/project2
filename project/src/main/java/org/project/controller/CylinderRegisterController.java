package org.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.contract.ContractCylinderRegister;
import org.project.model.Dto.CylinderRegisterDto.*;
import org.project.model.LoginUser;
import org.project.service.ContractCylinderRegisterService;
import org.project.util.CylinderExcelImporter;
import org.project.util.CylinderTemplateGenerator;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 气瓶登记追踪控制器
 */
@Slf4j
@RestController
@RequestMapping("/cylinder/register")
@RequiredArgsConstructor
public class CylinderRegisterController {

    private final ContractCylinderRegisterService registerService;
    private final CylinderExcelImporter excelImporter;
    private final CylinderTemplateGenerator templateGenerator;
    
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;
    
    @Value("${file.upload.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    /**
     * 登记气瓶
     */
    @PostMapping("/add")
    public Result<Long> registerCylinder(@Validated @RequestBody CylinderRegisterDto dto,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Long registerId = registerService.registerCylinder(dto, userId);
            return Result.success(registerId);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("气瓶登记失败", e);
            return Result.error(500, "气瓶登记失败：" + e.getMessage());
        }
    }

    /**
     * 批量登记气瓶
     */
    @PostMapping("/batch")
    public Result<Integer> batchRegister(@Validated @RequestBody List<CylinderRegisterDto> dtoList,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Integer successCount = registerService.batchRegister(dtoList, userId);
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("批量登记气瓶失败", e);
            return Result.error(500, "批量登记失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询气瓶登记列表
     */
    @PostMapping("/list")
    public Result<IPage<CylinderRegisterListDto>> queryRegisterPage(@RequestBody CylinderRegisterQueryDto queryDto,
                                                                    Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            // TODO: 如果是单位用户，自动限定为本单位的气瓶
            IPage<CylinderRegisterListDto> page = registerService.queryRegisterPage(queryDto, userId);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询气瓶列表失败", e);
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取气瓶登记详情
     */
    @GetMapping("/detail/{registerId}")
    public Result<CylinderRegisterDetailDto> getRegisterDetail(@PathVariable Long registerId) {
        try {
            CylinderRegisterDetailDto detail = registerService.getRegisterDetail(registerId);
            return Result.success(detail);
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("获取气瓶详情失败", e);
            return Result.error(500, "获取详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据气瓶编号查询详情
     */
    @GetMapping("/detail/by-no/{cylinderNo}")
    public Result<CylinderRegisterDetailDto> getDetailByCylinderNo(@PathVariable String cylinderNo) {
        try {
            CylinderRegisterDetailDto detail = registerService.getDetailByCylinderNo(cylinderNo);
            return Result.success(detail);
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("根据编号查询气瓶详情失败", e);
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新气瓶状态
     */
    @PutMapping("/status")
    public Result<Void> updateCylinderStatus(@Validated @RequestBody CylinderStatusUpdateDto dto,
                                            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            registerService.updateCylinderStatus(dto, userId);
            return Result.success(null);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新气瓶状态失败", e);
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 记录气瓶检验
     */
    @PostMapping("/inspect")
    public Result<Void> recordInspection(@Validated @RequestBody CylinderInspectDto dto,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            registerService.recordInspection(dto, userId);
            return Result.success(null);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("记录气瓶检验失败", e);
            return Result.error(500, "记录失败：" + e.getMessage());
        }
    }

    /**
     * 查询即将到期的气瓶
     */
    @GetMapping("/inspect/due-soon")
    public Result<List<ContractCylinderRegister>> getInspectDueSoon(@RequestParam(defaultValue = "30") Integer days) {
        try {
            List<ContractCylinderRegister> list = registerService.getInspectDueSoon(days);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询即将到期气瓶失败", e);
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询已过期的气瓶
     */
    @GetMapping("/inspect/overdue")
    public Result<List<ContractCylinderRegister>> getInspectOverdue() {
        try {
            List<ContractCylinderRegister> list = registerService.getInspectOverdue();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询已过期气瓶失败", e);
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询指定合同下的所有气瓶
     */
    @GetMapping("/contract/{contractId}")
    public Result<List<ContractCylinderRegister>> getByContractId(@PathVariable Long contractId) {
        try {
            List<ContractCylinderRegister> list = registerService.getByContractId(contractId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询合同气瓶失败", e);
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 下载气瓶导入模板
     */
    @GetMapping("/template/download")
    public Result<String> downloadTemplate() {
        try {
            // 生成Excel模板
            byte[] templateBytes = templateGenerator.generateCylinderTemplate();
            
            // 保存文件
            String datePath = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "template/" + datePath;
            java.nio.file.Path dirPath = java.nio.file.Paths.get(uploadBasePath, relativePath);
            
            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
            }
            
            String filename = "气瓶导入模板_" + System.currentTimeMillis() + ".xlsx";
            java.nio.file.Path filePath = dirPath.resolve(filename);
            java.nio.file.Files.write(filePath, templateBytes);
            
            String fileUrl = urlPrefix + "/" + relativePath + "/" + filename;
            log.info("气瓶模板生成成功：{}", fileUrl);
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("生成模板失败", e);
            return Result.error(500, "生成模板失败：" + e.getMessage());
        }
    }

    /**
     * 从 Excel 批量导入气瓶
     */
    @PostMapping("/import/excel")
    public Result<ImportResult> importFromExcel(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("contractId") Long contractId,
            @RequestParam("cylinderTypeId") Long cylinderTypeId,
            Authentication authentication) {
        try {
            // 校验文件格式
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return Result.error(400, "只支持Excel格式（.xlsx/.xls）");
            }
            
            // 解析Excel
            CylinderExcelImporter.ImportResult importResult = 
                excelImporter.importFromExcel(file.getInputStream(), contractId, cylinderTypeId);
            
            if (importResult.getCylinders().isEmpty()) {
                return Result.error(400, "没有有效的数据可导入");
            }
            
            // 批量保存
            Long userId = getUserIdFromAuth(authentication);
            int savedCount = registerService.batchRegister(importResult.getCylinders(), userId);
            
            // 返回结果
            ImportResult result = new ImportResult();
            result.setSuccessCount(savedCount);
            result.setFailureCount(importResult.getFailureCount());
            result.setErrors(importResult.getErrors().stream()
                .map(e -> new ErrorRow(e.getRowNum(), e.getErrorMsg()))
                .collect(java.util.stream.Collectors.toList()));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("导入气瓶失败", e);
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 从Authentication中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            if (loginUser.getUserId() == null) {
                throw new IllegalStateException("认证信息缺少用户ID");
            }
            return loginUser.getUserId();
        }

        if (principal instanceof UserDetails) {
            throw new IllegalStateException("认证principal不是LoginUser，无法获取用户ID");
        }

        throw new IllegalStateException("无法识别的认证信息类型：" + principal.getClass().getName());
    }

    /**
     * 导入结果 DTO
     */
    public static class ImportResult {
        private int successCount;
        private int failureCount;
        private List<ErrorRow> errors;

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
     * 错误行 DTO
     */
    public static class ErrorRow {
        private int rowNum;
        private String errorMsg;

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
