package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.model.Dto.CylinderTypeOptionDto;
import org.project.service.CylinderTypeService;
import org.project.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cylinder")
@RequiredArgsConstructor
public class CylinderController {

    private final CylinderTypeService cylinderTypeService;

    @GetMapping("/typeList")
    public Result<List<CylinderTypeOptionDto>> getCylinderTypeList() {
        List<CylinderTypeOptionDto> options = cylinderTypeService.getEnabledTypeOptions();
        return Result.success(options);
    }
}
