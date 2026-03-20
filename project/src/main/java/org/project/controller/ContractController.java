package org.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.Dto.ContractDto.*;
import org.project.model.Dto.ContractDto.ContractAuditDto.*;
import org.project.model.LoginUser;
import org.project.model.User;
import org.project.model.contract.Contract;
import org.project.service.*;
import org.project.util.Result;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 合同管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final FileUploadService fileUploadService;
    private final ExcelImportService excelImportService;
    private final UserService userService;



    /**
     * 获取即将到期的合同
     * @param unitId 单位ID（可选）
     * @return 即将到期的合同列表
     */
    @GetMapping("/expiring")
    public Result<List<Contract>> getExpiringContracts(@RequestParam(required = false) Long unitId,Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        User user = userService.getUserById(userId);
        Long unitIdFromeAuthentication=user.getUnitId();
        List<Contract> contracts = contractService.getExpiringContracts(unitIdFromeAuthentication);
        return Result.success(contracts);
    }


    @GetMapping("/dashboard/stats")
    public Result<ContractService.DashboardStatsDto> getDashboardStats(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        ContractService.DashboardStatsDto stats = contractService.getDashboardStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取新建合同初始化数据
     * 前端打开"新建合同"页面时调用，获取甲乙方信息和预生成的合同编号
     */
    @GetMapping("/init")
    public Result<ContractInitDto> getContractInitData(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            ContractInitDto initData = contractService.getContractInitData(userId);
            return Result.success(initData);
        } catch (UnsupportedOperationException e) {
            return Result.error(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取合同初始化数据失败", e);
            return Result.error(400, "获取合同初始化数据失败：" + e.getMessage());
        }
    }

    /**
     * 创建合同
     */
    @PostMapping("/create")
    public Result<Long> createContract(@Validated @RequestBody ContractCreateDto dto,
                                       Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Long contractId = contractService.createContract(dto, userId);
            return Result.success(contractId);
        } catch (IllegalArgumentException e) {
            return Result.error(401,e.getMessage());
        } catch (Exception e) {
            log.error("创建合同失败", e);
            return Result.error(400,"创建合同失败：" + e.getMessage());
        }
    }

    /**
     * 更新合同
     */
    @PutMapping("/update")
    public Result<Void> updateContract(@Validated @RequestBody ContractUpdateDto dto,
                                       Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            contractService.updateContract(dto, userId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新合同失败", e);
            return Result.error(400,"更新合同失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询合同列表
     */
    @PostMapping("/list")
    public Result<IPage<ContractListItemDto>> queryContractPage(@RequestBody ContractQueryDto queryDto,
                                                                Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            IPage<ContractListItemDto> page = contractService.queryContractPage(userId, queryDto);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询合同列表失败", e);
            return Result.error(400, "查询合同列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/detail/{contractId}")
    public Result<ContractDetailDto> getContractDetail(@PathVariable Long contractId) {
        try {
            ContractDetailDto detail = contractService.getContractDetail(contractId);
            return Result.success(detail);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取合同详情失败", e);
            return Result.error(400, "获取合同详情失败：" + e.getMessage());
        }
    }
    /**
     * 终止合同
     */
    @PostMapping("/terminate")
    public Result<Void> terminateContract(@Validated @RequestBody ContractTerminateDto dto,
                                          Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            contractService.terminateContract(dto, userId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("终止合同失败", e);
            return Result.error(400, "终止合同失败：" + e.getMessage());
        }
    }

    /**
     * 下载合同PDF
     */
    @GetMapping("/download/{contractId}")
    public Result<String> downloadContractPdf(@PathVariable Long contractId) {
        try {
            String pdfUrl = contractService.downloadContractPdf(contractId);
            return Result.success(pdfUrl);
        } catch (UnsupportedOperationException e) {
            return Result.error(400, "PDF生成功能暂未实现");
        } catch (Exception e) {
            log.error("下载合同PDF失败", e);
            return Result.error(400, "下载合同PDF失败：" + e.getMessage());
        }
    }

    /**
     * 上传合同扫描件
     */
    @PostMapping("/upload/scan")
    public Result<String> uploadScanFile(@RequestParam("file") MultipartFile file) {
        try {
            // 校验文件类型
            if (!fileUploadService.validateFileType(file, "pdf")) {
                return Result.error(400, "只支持PDF格式");
            }

            // 校验文件大小（最大20MB）
            if (!fileUploadService.validateFileSize(file, 20 * 1024 * 1024)) {
                return Result.error(400, "文件大小不能超过20MB");
            }

            String fileUrl = fileUploadService.uploadFile(file, "contract");
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return Result.error(400, "上传文件失败：" + e.getMessage());
        }
    }

    /**
     * 批量导入合同
     */
    @PostMapping("/import")
    public Result<ExcelImportService.ImportResult> importContracts(@RequestParam("file") MultipartFile file,
                                                                   Authentication authentication) {
        try {
            // 校验文件类型
            if (!fileUploadService.validateFileType(file, "xlsx", "xls")) {
                return Result.error(400, "只支持Excel格式（.xlsx/.xls）");
            }

            Long userId = getUserIdFromAuth(authentication);
            ExcelImportService.ImportResult result = excelImportService.importContracts(file, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量导入失败", e);
            return Result.error(400, "批量导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template/download")
    public Result<String> downloadTemplate() {
        try {
            String templateUrl = excelImportService.downloadTemplate();
            return Result.success(templateUrl);
        } catch (UnsupportedOperationException e) {
            return Result.error(400, "模板下载功能暂未实现");
        } catch (Exception e) {
            log.error("下载模板失败", e);
            return Result.error(400, "下载模板失败：" + e.getMessage());
        }
    }

    // ==================== 合同审核流程API ====================

    /**
     * 管理员退回合同
     * 将待确认的合同退回给单位用户，需填写退回原因
     */
    @PostMapping("/reject")
    public Result<Void> rejectContract(@Validated @RequestBody RejectDto dto,
                                       Authentication authentication) {
        try {
            Long operatorId = getUserIdFromAuth(authentication);
            contractService.rejectContract(dto.getContractId(), dto.getRejectReason(), operatorId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("退回合同失败", e);
            return Result.error(400, "退回合同失败：" + e.getMessage());
        }
    }

    /**
     * 管理员确认合同
     * 审核通过，合同进入待缴费状态
     */
    @PostMapping("/confirm")
    public Result<Void> confirmContract(@Validated @RequestBody ConfirmDto dto,
                                        Authentication authentication) {
        try {
            Long operatorId = getUserIdFromAuth(authentication);
            contractService.confirmContract(dto.getContractId(), dto.getRemark(), operatorId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("确认合同失败", e);
            return Result.error(400, "确认合同失败：" + e.getMessage());
        }
    }

    /**
     * 单位用户重新提交合同
     * 被退回后修改完成，重新提交审核
     */
    @PostMapping("/resubmit")
    public Result<Void> resubmitContract(@Validated @RequestBody ResubmitDto dto,
                                         Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            contractService.resubmitContract(dto.getContractId(), userId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("重新提交合同失败", e);
            return Result.error(400, "重新提交合同失败：" + e.getMessage());
        }
    }

    /**
     * 用户提交缴费信息
     * 合同确认后，用户提交缴费信息等待管理员审核
     */
    @PostMapping("/payment/submit")
    public Result<Void> submitPayment(@Validated @RequestBody PaymentSubmitDto dto,
                                      Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            contractService.submitPayment(dto.getContractId(), dto.getPaymentMethod(),
                    dto.getPaymentAmount(), dto.getPaymentVoucherUrl(), dto.getRemark(), userId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("提交缴费信息失败", e);
            return Result.error(400, "提交缴费信息失败：" + e.getMessage());
        }
    }

    /**
     * 管理员审核缴费
     *
     */
    @PostMapping("/payment/audit")
    public Result<Void> auditPayment(@Validated @RequestBody PaymentAuditDto dto,
                                     Authentication authentication) {
        try {
            Long operatorId = getUserIdFromAuth(authentication);
            contractService.auditPayment(dto.getContractId(), dto.getApproved(),
                    dto.getAuditRemark(), operatorId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("审核缴费失败", e);
            return Result.error(400, "审核缴费失败：" + e.getMessage());
        }
    }

    @PostMapping("/sign")
    public Result<Void> signContract(@Validated @RequestBody SignDto dto,
                                    Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            contractService.signContract(dto.getContractId(), userId);
            return Result.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("签署合同失败", e);
            return Result.error(400, "签署合同失败：" + e.getMessage());
        }
    }
    /**
     * 从Authentication中获取用户ID
     *
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

        // principal 不是 LoginUser 的情况下，不再依赖 UserDetails 类型判断
        throw new IllegalStateException("无法识别的认证信息类型：" + principal.getClass().getName());
    }
}
