package org.project.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.project.mapper.UnitPersonnelMapper;
import org.project.model.UnitPersonnel;
import org.project.service.UnitPersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitPersonnelServiceImpl implements UnitPersonnelService {
    @Autowired
    private UnitPersonnelMapper unitPersonnelMapper;

    @Override
    public IPage<UnitPersonnel> getPersonnelList(int page, int size, String keyword, Long unitId, String department, String role) {
        Page<UnitPersonnel> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<UnitPersonnel> queryWrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(UnitPersonnel::getPersonName, keyword)
                    .or().like(UnitPersonnel::getPhone, keyword)
                    .or().like(UnitPersonnel::getDepartment, keyword);
        }

        if (unitId != null) {
            queryWrapper.eq(UnitPersonnel::getUnitId, unitId);
        }

        if (department != null && !department.isEmpty()) {
            queryWrapper.like(UnitPersonnel::getDepartment, department);
        }

        // 角色筛选
        if (role != null && !role.isEmpty()) {
            if ("admin".equals(role)) {
                queryWrapper.eq(UnitPersonnel::getRoleType, 1); // 管理员
            } else if ("user".equals(role)) {
                queryWrapper.eq(UnitPersonnel::getRoleType, 2); // 操作员
            }
        }

        return unitPersonnelMapper.selectPersonnelWithUnitName(pageInfo, queryWrapper);
    }

    @Override
    public List<UnitPersonnel> getAllPersonnel() {
        return unitPersonnelMapper.selectList(null);
    }

    @Override
    public UnitPersonnel getPersonnelById(Long personnelId) {
        return unitPersonnelMapper.selectById(personnelId);
    }

    @Override
    public UnitPersonnel getPersonnelByPhone(String phone) {
        LambdaQueryWrapper<UnitPersonnel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitPersonnel::getPhone, phone);
        return unitPersonnelMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean savePersonnel(UnitPersonnel personnel) {
        return unitPersonnelMapper.insert(personnel) > 0;
    }

    @Override
    public Boolean updatePersonnel(UnitPersonnel personnel) {
        return unitPersonnelMapper.updateById(personnel) > 0;
    }

    @Override
    public Boolean deletePersonnel(Long personnelId) {
        return unitPersonnelMapper.deleteById(personnelId) > 0;
    }

    @Override
    public Boolean updatePersonnelStatus(Long personnelId, Integer status) {
        UnitPersonnel personnel = new UnitPersonnel();
        personnel.setPersonnelId(personnelId);
        personnel.setStatus(status);
        return unitPersonnelMapper.updateById(personnel) > 0;
    }
}