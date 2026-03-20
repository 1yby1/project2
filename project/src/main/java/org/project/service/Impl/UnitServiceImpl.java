package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.mapper.unitMapper.UnitMapper;
import org.project.model.Dto.UnitListDto;
import org.project.model.Unit.Unit;
import org.project.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {

    @Autowired
    private UnitMapper unitMapper;

    @Override
    public List<UnitListDto> getEnabledUnitList() {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Unit::getStatus, 1)
                .orderBy(true, true, Unit::getUnitId);

        List<Unit> units = unitMapper.selectList(queryWrapper);

        return units.stream()
                .map(unit -> new UnitListDto(unit.getUnitId(), unit.getUnitName()))
                .collect(Collectors.toList());
    }
    @Override
    public IPage<Unit> getUnitPage(int page, int size, Map<String, Object> params) {
        Page<Unit> pageParam = new Page<>(page, size);
        return unitMapper.selectUnitPageWithContractCount(pageParam, params);
    }
    @Override
    public String getUnitNameById(Long unitId) {
        Unit unit = unitMapper.selectById(unitId);
        return unit != null ? unit.getUnitName() : null;
    }

    @Override
    public Unit getUnitById(Long unitId) {
        Unit unit = unitMapper.selectById(unitId);
        if (unit != null) {
            Integer contractCount = unitMapper.countContractsByUnitId(unitId);
            unit.setContractCount(contractCount == null ? 0 : contractCount);
        }
        return unit;
    }

}
