package org.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.Dto.UnitListDto;
import org.project.model.Unit.Unit;
import org.project.service.UnitService;
import org.project.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public Result<IPage<Unit>> getUnitList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer creditLevel) {

        // 使用HashMap避免Map.of()的NullPointerException问题
        Map<String, Object> params = new HashMap<>();
        if (keyword != null) {
            params.put("keyword", keyword);
        }
        if (status != null) {
            params.put("status", status);
        }
        if (creditLevel != null) {
            params.put("creditLevel", creditLevel);
        }

        IPage<Unit> unitPage = unitService.getUnitPage(page, size, params);
        return Result.success(unitPage);
    }

    @PutMapping("/update")
    public Result<Boolean> updateUnit(@RequestBody Unit unit) {
        boolean success = unitService.updateById(unit);
        return success ? Result.success(true) : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUnit(@PathVariable("id") Integer unitId) {
        boolean success = unitService.removeById(unitId);
        return success ? Result.success(true) : Result.error(500, "删除失败");
    }

    @PostMapping("/add")
    public Result<Boolean> addUnit(@RequestBody Unit unit) {
        // 自动生成单位编码：U + 时间戳 + 5位随机数
        String unitCode = "U" + System.currentTimeMillis() + String.format("%05d", (int) (Math.random() * 100000));
        unit.setUnitCode(unitCode);

        // 设置默认值
        if (unit.getRegionCode() == null) {
            unit.setRegionCode("NJ");
        }
        if (unit.getCreditLevel() == null) {
            unit.setCreditLevel(3);
        }
        if (unit.getStatus() == null) {
            unit.setStatus(1);
        }
        if (unit.getServiceStatus() == null) {
            unit.setServiceStatus(1);
        }

        boolean success = unitService.save(unit);
        return success ? Result.success(true) : Result.error(500, "新增失败");
    }

    /**
     * 获取所有启用的单位列表
     */
    @GetMapping("/simplelist")
    public Result<List<UnitListDto>> getUnitList() {
        List<UnitListDto> unitList = unitService.getEnabledUnitList();
        return Result.success(unitList);
    }

    @GetMapping("/{unitId}")
    public Result<Unit> getUnitById(@PathVariable Long unitId) {
        Unit unit = unitService.getUnitById(unitId);
        if (unit == null) {
            return Result.error(404, "单位不存在");
        }
        return Result.success(unit);
    }

}