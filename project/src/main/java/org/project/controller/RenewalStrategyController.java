package org.project.controller;

import org.project.model.renewal.RenewalStrategy;
import org.project.service.RenewalStrategyService;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/renewal/strategies")
public class RenewalStrategyController {

    @Autowired
    private RenewalStrategyService renewalStrategyService;

    @GetMapping
    public Result<List<RenewalStrategy>> getAllStrategies() {
        List<RenewalStrategy> strategies = renewalStrategyService.getAllStrategies();
        return Result.success(strategies);
    }

    @GetMapping("/{id}")
    public Result<RenewalStrategy> getStrategyById(@PathVariable Long id) {
        RenewalStrategy strategy = renewalStrategyService.getStrategyById(id);
        if (strategy != null) {
            return Result.success(strategy);
        }
        return Result.error(404, "Strategy not found");
    }

    @PostMapping
    public Result<String> createStrategy(@RequestBody RenewalStrategy strategy) {
        boolean success = renewalStrategyService.saveStrategy(strategy);
        if (success) {
            return Result.success("Strategy created successfully");
        }
        return Result.error(500, "Failed to create strategy");
    }

    @PutMapping("/{id}")
    public Result<String> updateStrategy(@PathVariable Long id, @RequestBody RenewalStrategy strategy) {
        strategy.setStrategyId(id);
        boolean success = renewalStrategyService.updateStrategy(strategy);
        if (success) {
            return Result.success("Strategy updated successfully");
        }
        return Result.error(500, "Failed to update strategy");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteStrategy(@PathVariable Long id) {
        boolean success = renewalStrategyService.deleteStrategy(id);
        if (success) {
            return Result.success("Strategy deleted successfully");
        }
        return Result.error(500, "Failed to delete strategy");
    }
}
