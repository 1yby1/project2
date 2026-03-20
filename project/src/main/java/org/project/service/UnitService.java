package org.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.project.model.Dto.UnitListDto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.project.model.Unit.Unit;
import java.util.List;
import java.util.Map;

public interface UnitService extends IService<Unit> {
    /**
     * 获取所有启用的单位列表（仅返回ID和名称）
     */
    List<UnitListDto> getEnabledUnitList();

    IPage<Unit> getUnitPage(int page, int size, Map<String, Object> params);

    String getUnitNameById(Long unitId);

    Unit getUnitById(Long unitId);
}
