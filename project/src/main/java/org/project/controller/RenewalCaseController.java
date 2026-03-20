package org.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.renewal.RenewalCase;
import org.project.service.RenewalCaseService;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/renewal/cases")
public class RenewalCaseController {

    @Autowired
    private RenewalCaseService renewalCaseService;

    @GetMapping
    public Result<IPage<RenewalCase>> getPageList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Integer status) {
        IPage<RenewalCase> pageData = renewalCaseService.getPageList(page, size, unitId, status);
        return Result.success(pageData);
    }

    @GetMapping("/{id}")
    public Result<RenewalCase> getCaseById(@PathVariable Long id) {
        RenewalCase renewalCase = renewalCaseService.getCaseById(id);
        if (renewalCase != null) {
            return Result.success(renewalCase);
        }
        return Result.error(404, "Case not found");
    }

    @PostMapping
    public Result<String> createCase(@RequestBody RenewalCase renewalCase, @RequestParam(required = false) Long contractId) {
        // 添加日志以调试金额传递问题
        System.out.println("=== DEBUG: Controller received renewal case ===");
        System.out.println("Unit ID: " + renewalCase.getUnitId());
        System.out.println("Strategy ID: " + renewalCase.getStrategyId());
        System.out.println("Status: " + renewalCase.getStatus());
        System.out.println("Original Amount: " + renewalCase.getOriginalAmount());
        System.out.println("Discount Amount: " + renewalCase.getDiscountAmount());
        System.out.println("Final Amount: " + renewalCase.getFinalAmount());
        System.out.println("Contract ID: " + contractId);

        boolean success;
        if (contractId != null) {
            success = renewalCaseService.saveCaseWithContract(renewalCase, contractId);
        } else {
            success = renewalCaseService.saveCase(renewalCase);
        }
        if (success) {
            return Result.success("Case created successfully");
        }
        return Result.error(500, "Failed to create case");
    }

    @PutMapping("/{id}")
    public Result<String> updateCase(@PathVariable Long id, @RequestBody RenewalCase renewalCase) {
        renewalCase.setCaseId(id);
        boolean success = renewalCaseService.updateCase(renewalCase);
        if (success) {
            return Result.success("Case updated successfully");
        }
        return Result.error(500, "Failed to update case");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = renewalCaseService.updateStatus(id, status);
        if (success) {
            return Result.success("Status updated successfully");
        }
        return Result.error(500, "Failed to update status");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteCase(@PathVariable Long id) {
        boolean success = renewalCaseService.deleteCase(id);
        if (success) {
            return Result.success("Case deleted successfully");
        }
        return Result.error(500, "Failed to delete case");
    }
}
