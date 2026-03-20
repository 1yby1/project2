package org.project.service;

import org.project.model.Dto.CylinderTypeOptionDto;

import java.util.List;

public interface CylinderTypeService {
    List<CylinderTypeOptionDto> getEnabledTypeOptions();
}
